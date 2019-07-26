(ns rygb-demo.subs
  (:require
   [re-frame.core :as rf]
   [garden.units :refer [vmin vmax percent px]]
   [rygb-demo.util :as util]
   [rygb-demo.ui-config :refer [control-vmin diagonal]]
   [rygb.core :as rygb]))

;; Queries  -------------------------------------------------------
(rf/reg-sub
  ::rygb-map
  (fn [db _]
    (:rygb/map db)))

(rf/reg-sub
  ::rygb-string
  (fn [db _]
    (:rygb/string db)))

(rf/reg-sub
  ::rygb-string-input
  (fn [db _]
    (:rygb/input db)))

(rf/reg-sub
  ::rygb-string-input-valid?
  (fn [db _]
    (-> db :rygb/input util/input-valid?)))

(defn sva* [db kw]
  (js/Math.round (* 1e2 (-> db :rygb/map kw))))

(rf/reg-sub
  ::val
  (fn [db _]
    (sva* db :v)))

(rf/reg-sub
  ::sat
  (fn [db _]
    (sva* db :s)))

(rf/reg-sub
  ::alpha
  (fn [db _]
    (sva* db :a)))

(rf/reg-sub
  ::angle
  (fn [db _]
    (-> db :rygb/string rygb/rygb->rygb-angle)))

(rf/reg-sub
  ::blaster
  (fn [db _]
    (:blaster db)))

(rf/reg-sub
  ::ace
  (fn [db _]
    (:rygb/ace db)))

(rf/reg-sub
 ::rygb->hex
 (fn [db _]
   (rygb/rygb->hex (:rygb/string db))))

(rf/reg-sub
 ::window
 (fn [db _]
   (:window db)))

(defn as-str [{u :unit m :magnitude}]
  (str m (name u)))

(rf/reg-sub
 ::tempest
 (fn [db _]
   (:tempest db)))

(defn slider-bg-gradient [db]
  (let [background-color (rygb/rygb->hex {:h (-> db :rygb/map :h)})]
    (if (-> db :window :orientation (= :landscape))
      {:background-image (str "linear-gradient(to bottom, " background-color ", white)") }
      {:background-image (str "linear-gradient(to left, " background-color ", white)")})))

(defn sva-slider-style [db k]
  (if (-> db :window :orientation (= :landscape))
    {:bottom (-> db :rygb/map k (* 100) (str "%"))}
    {:left (-> db :rygb/map k (* 100) (str "%"))}))

(rf/reg-sub
 ::s-control
 (fn [db _]
   (sva-slider-style db :s)))

(rf/reg-sub
 ::v-control
 (fn [db _]
   (sva-slider-style db :v)))

(rf/reg-sub
 ::s-track
 (fn [db _]
   (slider-bg-gradient db)))

(rf/reg-sub
 ::v-track
 (fn [db _]
   (if (-> db :window :orientation (= :landscape))
      {:background-image (str "linear-gradient(to bottom, white," (rygb/rygb->hex "v23") ")") }
      {:background-image (str "linear-gradient(to left, white," (rygb/rygb->hex "v23") ")")})))

(rf/reg-sub
 ::font-family-idx
 (fn [db _]
   (:font-family-idx db)))

(rf/reg-sub
 ::font-family
 (fn [db _]
   (:font-family db)))

(rf/reg-sub
 ::info?
 (fn [db _]
   (:info? db)))
