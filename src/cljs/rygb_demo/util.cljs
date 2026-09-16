(ns rygb-demo.util
  (:require [rygb.core :as rygb]))

(def max-hue-part 7)

(defn hue-parts-valid? [{:keys [h]}]
  (or (nil? h)
      (every? #(<= % max-hue-part) (vals h))))

(defn input-valid? [s]
  (boolean (some-> s rygb/rygb->map hue-parts-valid?)))

(defn clamp-hue-parts [h]
  (let [largest-part (apply max (vals h))]
    (if (<= largest-part max-hue-part)
      h
      (reduce-kv (fn [clamped-hue color part]
                   (assoc clamped-hue color
                          (js/Math.ceil (* max-hue-part
                                           (/ part largest-part)))))
                 (empty h)
                 h))))

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
