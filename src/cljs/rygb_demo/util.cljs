(ns rygb-demo.util
  (:require [rygb.core :as rygb]))

(def max-hue-part 7)

(defn- gcd [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

(def ^:private bounded-hue-part-pairs
  (for [part-1 (range 1 (inc max-hue-part))
        part-2 (range 1 (inc max-hue-part))
        :when (= 1 (gcd part-1 part-2))]
    [part-1 part-2]))

(defn- mixture-fraction [[part-1 part-2]]
  (/ part-1 (+ part-1 part-2)))

(defn- closer-pair [target-fraction closest candidate]
  (let [closest-distance (js/Math.abs
                          (- target-fraction (mixture-fraction closest)))
        candidate-distance (js/Math.abs
                            (- target-fraction (mixture-fraction candidate)))]
    (if (or (< candidate-distance closest-distance)
            (and (= candidate-distance closest-distance)
                 (< (apply + candidate) (apply + closest))))
      candidate
      closest)))

(defn hue-parts-valid? [{:keys [h]}]
  (or (nil? h)
      (every? #(<= % max-hue-part) (vals h))))

(defn input-valid? [s]
  (boolean (some-> s rygb/rygb->map hue-parts-valid?)))

(defn clamp-hue-parts [h]
  (if (= 2 (count h))
    (let [colors (vec (keys h))
          parts (mapv h colors)
          target-fraction (mixture-fraction parts)
          closest-parts (reduce (partial closer-pair target-fraction)
                                (first bounded-hue-part-pairs)
                                (rest bounded-hue-part-pairs))]
      (zipmap colors closest-parts))
    h))

(defn display-rygb-string [{:keys [h] :as m}]
  (rygb/rygb->string
   (cond-> (dissoc m :a)
     (= 1.0 (:s m)) (dissoc :s)
     (= 1.0 (:v m)) (dissoc :v)
     h (assoc :h (clamp-hue-parts h)))))

(defn px [n]
  (str n "px"))

(defn gui-rygb-map [db s]
  (when-let [{:keys [h s v a] :or {a 1.0}} (rygb/rygb->map s)]
    (when (hue-parts-valid? {:h h})
      (if (and (nil? h) (nil? s) (not (nil? v)))
        (let [h (or (some-> db :rygb/map :h) {:r 1})
              s 0]
          {:h h :s s :v v :a a})
        {:h h :s (or s 1.0) :v (or v 1.0) :a a}))))
