(ns rygb-demo.util
  (:require [rygb.core :as rygb]))

(defn input-valid? [s]
  (if (rygb/rygb->map s) true false))

(defn px [n]
  (str n "px"))

(defn gui-rygb-map [db s]
  (when-let [{:keys [h s v a] :or {a 1.0}} (rygb/rygb->map s)]
    (if (and (nil? h) (nil? s) (not (nil? v)))
      (let [h (or (some-> db :rygb/map :h) {:r 1})
            s 0]
        {:h h :s s :v v :a a})
      {:h h :s (or s 1.0) :v (or v 1.0) :a a})))

