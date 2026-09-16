(ns rygb-demo.recovered
  "Color-wheel helpers recovered from the demo's shipped advanced build."
  (:require [rygb.core :as rygb]))

(def ^:private positions {:r 0 :y 90 :g 180 :b 270})
(def ^:private pairs [[:r :y] [:y :g] [:g :b] [:b :r]])
(def ^:private reversed-pairs
  (into {} (map (fn [pair] [(reverse pair) pair]) pairs)))

(defn- round [n]
  #?(:cljs (js/Math.round n)
     :clj (Math/round (double n))))

(defn- gcd [a b]
  (if (zero? b) a (recur b (mod a b))))

(defn rygb->rygb-angle
  "Returns a rounded RYGB wheel angle from a library-supported string or map.
  Primaries are 90 degrees apart; grayscale and invalid strings return nil."
  [arg]
  (when-let [h (some-> arg rygb/rygb->map :h)]
    (let [[h1 h2*] (or (get reversed-pairs (keys h)) (keys h))
          h2 (or h2* h1)
          p (assoc positions :r (if (and (:r h) (:b h)) 360 0))
          fraction (- 1 (/ (double (get h h1))
                          (+ (get h h1) (get h h2))))]
      (round (+ (get p h1)
                (* fraction #?(:cljs (js/Math.abs (- (get p h1) (get p h2)))
                               :clj (Math/abs (long (- (get p h1) (get p h2)))))))))))

(defn rygb-angle->ace-map
  "Recovers the original ratios and marker scale data for a wheel angle.
  Angles wrap at 360 degrees; component distances round before reduction."
  [angle]
  (let [angle (mod angle 360)
        quadrant (int (/ angle 90))
        pair (get pairs quadrant)
        primary? (zero? (mod angle 90))
        ks (if primary? [(first pair)] pair)
        hue1 (round (- (if primary? (* quadrant 90) (* (inc quadrant) 90))
                       angle))
        hue2 (round (- angle (* quadrant 90)))]
    (cond
      (= hue1 45) {:hue1 1 :hue2 1 :ks ks}
      (zero? hue1) {:hue1 1 :ks ks}
      :else (let [divisor (gcd hue1 hue2)
                  h1 (/ hue1 divisor)
                  h2 (/ hue2 divisor)]
              {:hue1 h1 :hue2 h2 :ks ks
               :scale (if (> h1 h2)
                        {:hue2 (/ (double h2) h1)}
                        {:hue1 (/ (double h1) h2)})}))))

(defn rygb-angle->rygb-hue-map
  "Returns the original reduced hue ratio map for a wheel angle."
  [angle]
  (let [{:keys [hue1 hue2 ks]} (rygb-angle->ace-map angle)]
    (zipmap ks [hue1 hue2])))
