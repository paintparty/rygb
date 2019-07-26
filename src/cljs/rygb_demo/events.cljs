(ns rygb-demo.events
  (:require
   [clojure.string :as string]
   [goog.object :as gobj]
   [re-frame.core :as rf]
   [rygb-demo.db :as db :refer [default-db]]
   [rygb-demo.util :as util]
   [rygb.hsva]
   [rygb-demo.ui-config :as ui-config
    :refer [calc-square-min calc-square-min-portrait calc-square-max calc-square-max-portrait diagonal]]
   [rygb.core :as rygb]))

(defn event [e]
 (if-let [t (gobj/getValueByKeys e "touches")]
   (gobj/get t 0)
   e))

(defn circle-center []
  (let [c (js/jQuery "#magic-circle")
        left (.-left (.offset c))
        top (.-top (.offset c))
        w (.outerWidth c)
        h (.outerHeight c)
        x (+ (/ w 2) left)
        y (+ (/ h 2) top)]
    [x y]))

(defn degrees->radians [n] (* n (/ js/Math.PI 180)))

(defn sin [n] (-> n degrees->radians js/Math.sin))

(defn side [r a] (* r (/ (sin a) (sin (- 180 45 a)))))

(defn quadrant [angle]
  (let [angle (-> angle (/ 90) js/Math.ceil)]
    (if (zero? angle) angle (dec angle))))

(defn blaster-angle [e]
  (let [event (event e)
        [center-x center-y] (circle-center)
        pos-x (.-pageX event)
        pos-y (.-pageY event)
        delta-x (- center-x pos-x)
        delta-y (- center-y pos-y)
        angle* (- (* (js/Math.atan2 delta-y delta-x) (/ 180 js/Math.PI)) 90)
        angle (if (< angle* 0) (+ 360 angle*) angle*)]
    (js/Math.round angle)))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   (let [m (->> default-db :rygb/string (util/gui-rygb-map default-db))
         angle (-> m
                   rygb/rygb->string
                   rygb/rygb->rygb-angle)]
     {:rygb/map m
      :rygb/string (:rygb/string default-db)
      :rygb/input (:rygb/string default-db)
      :rygb/ace (rygb.hsva/rygb-angle->ace-map angle)
      :dragging-hue? false
      :dragging-sat? false
      :dragging-val? false
      :dragging-alpha? false
      :touch-target nil
      :blaster-angle angle})))

(defn tempest-diameter [ww wh]
  (let [vmin-px (min ww wh)
        vmax-px (max ww wh)
        landscape? (> (/ ww wh) 1)
        square-min-px ((if landscape? calc-square-min calc-square-min-portrait) vmin-px)
        square-max-px ((if landscape? calc-square-max calc-square-max-portrait) vmax-px)]
    (min square-min-px square-max-px)))

(defn blaster
  [{:keys [db angle ww wh]
    :or {angle (:blaster-angle db)
         ww (-> db :window :w)
         wh (-> db :window :h)}}]
  (let [radius (/ (* 0.99 (tempest-diameter ww wh)) 2)
        mod-angle (mod angle 90)
        quadrant (quadrant angle)
        offset (if (zero? mod-angle)
                 (if (zero? angle) 0 "100%")
                 (side radius (js/Math.round mod-angle)))
        rotate (str "rotate(" (+ -45 angle) "deg)")
        opts [{:top 0 :left offset :transform (str "translate(-50%, -50%) " rotate)}
              {:top offset :right 0 :transform (str "translate(50%, -50%) " rotate)}
              {:bottom 0 :right offset :transform (str "translate(50%, 50%) " rotate)}
              {:bottom offset :left 0 :transform (str "translate(-50%, 50%) " rotate)}]
        tlbr (get opts quadrant)]
    tlbr))

(rf/reg-event-db
 ::resize
 (fn [db [_ _]]
   (let [ww js/window.innerWidth
         wh js/window.innerHeight
         orientation (if (< (/ ww wh) 1) :portrait :landscape)]
     (assoc db
            :window {:w ww :h wh :orientation orientation}
            :blaster (blaster {:db db :ww ww :wh wh})
            :tempest (/ (tempest-diameter ww wh) diagonal)))))

(rf/reg-event-db
 ::on-down
 (fn [db [_ [dragging?-key touch-target]]]
   (assoc db dragging?-key true :touch-target touch-target)))

(rf/reg-event-db
 ::on-up
 (fn [db [_ _]]
   (assoc db
          :dragging-hue? false
          :dragging-sat? false
          :dragging-val? false
          :dragging-alpha? false
          :touch-target nil)))

(defn sva-change [db v* kw]
   (let [v (-> v* js/parseInt (/ 100))
         m (assoc (:rygb/map db) kw v)
         s (rygb/rygb->string m)]
     (assoc db
            :rygb/input s
            :rygb/string s
            :rygb/map m)))

(rf/reg-event-db
 ::sat-change
 (fn [db [_ sat*]]
   (sva-change db sat* :s)))

(rf/reg-event-db
 ::val-change
 (fn [db [_ val*]]
   (sva-change db val* :v)))

(rf/reg-event-db
 ::alpha-change
 (fn [db [_ alpha*]]
   (sva-change db alpha* :a)))

(defn slider-percent [db e el]
  (if (-> db :window :orientation (= :landscape))
    (let [height (.outerHeight el)
          offset (.offset el)
          pct (- 1 (/ (- (.-pageY e) (.-top offset)) height))]
      pct)
    (let [width (.outerWidth el)
          offset (.offset el)
          pct (/ (- (.-pageX e) (.-left offset)) width)]
      pct)))

(defn update-slide [db id k e]
  (let [el (js/jQuery (str "#" id))
        slider-percent (slider-percent db e el)]
    (let [percent (cond
                    (<= slider-percent 0) 0
                    (>= slider-percent 1) 1
                    :else slider-percent)
          m (assoc (:rygb/map db) k percent)
          s (rygb/rygb->string m)]
      (assoc db
             :rygb/input s
             :rygb/string s
             :rygb/map m))))

(defn update-rygb! [db s m n]
  (assoc db
         :rygb/input s
         :rygb/string s
         :rygb/map m
         :rygb/ace (rygb.hsva/rygb-angle->ace-map n)
         :blaster-angle n
         :blaster (blaster {:db db :angle n})))

(rf/reg-event-db
 ::on-move
 (fn [db [_ e]]
   (cond
     (:dragging-hue? db)
     (let [n (-> e blaster-angle js/parseInt)
           m (assoc (:rygb/map db) :h (rygb/rygb-angle->rygb-hue-map n))
           s (rygb/rygb->string m)]
       (update-rygb! db s m n))

    (:dragging-sat? db)
    (update-slide db "s" :s e)

    (:dragging-val? db)
    (update-slide db "v" :v e)

     :else db)))

(rf/reg-event-db
 ::rygb-input-change
 (fn [db [_ s]]
   (if-let [m (util/gui-rygb-map db s)]
     (let [n (-> m rygb/rygb->string rygb/rygb->rygb-angle js/parseInt)]
       (update-rygb! db s m n))
     (assoc db :rygb/input s))))

(rf/reg-event-db
 ::show-info
 (fn [db [_ e]]
   (assoc db :info? true)))

(rf/reg-event-db
 ::close-info
 (fn [db [_ e]]
   (assoc db :info? false)))
