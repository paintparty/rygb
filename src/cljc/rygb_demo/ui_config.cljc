(ns rygb-demo.ui-config
  (:require
   [garden.units :refer [vmin vmax]])
  #?(:cljs (:require-macros [garden.units :refer [vmin vmax]])))

(def default-db
  {:rygb/string "br-s84-v69"
   :rygb/map {:h {:r 1 :b 1} :s 0.84 :v 0.69 :a 1.0}
   :rygb/input "br-s84-v69"})

(def bgc "#1f1f1f")

(def track-width 4)

(def ball-control-diameter 12)

(def control-vmin 0.44)

(def control-vmin-portrait 0.50)

(def diagonal (Math/sqrt 2))

(def square-min (vmin (* control-vmin diagonal 100)))

(def square-max (vmax (* control-vmin 100)))

(def square-min-portrait (vmin (* control-vmin-portrait diagonal 100)))

(def square-max-portrait (vmax (* control-vmin-portrait 100)))

(defn calc-square-min [vmin-px]
  (* vmin-px control-vmin diagonal))

(defn calc-square-max [vmax-px]
  (* vmax-px control-vmin))

(defn calc-square-min-portrait [vmin-px]
  (* vmin-px control-vmin-portrait diagonal))

(defn calc-square-max-portrait [vmax-px]
  (* vmax-px control-vmin-portrait))

(defn px [n]
  (str n "px"))
