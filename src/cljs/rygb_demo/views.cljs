(ns rygb-demo.views
  (:require
   [clojure.string :as string]
   [clojure.set :refer [map-invert]]
   [goog.object :as gobj]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [rygb.core :as rygb :refer [rygb->hex rygb->hex rygb->rgba-css rygb->rgb-css]]
   [rygb-demo.ui-config :as ui-config]
   [rygb-demo.util :as util]
   [rygb-demo.subs :as subs]
   [rygb-demo.events :as events]))

; stuff this in events like on-move
(defn on-down [e]
  (let [class (.. e -target -className)
        class-set (into #{} (string/split class #" "))
        id (.. e -target -id)]

    (cond
      (contains? class-set "hue")
      (rf/dispatch [::events/on-down [:dragging-hue? class]])

      (contains? class-set "s")
      (rf/dispatch [::events/on-down [:dragging-sat? id]])

      (contains? class-set "v")
      (rf/dispatch [::events/on-down [:dragging-val? id]])

      :else nil)))

(defn on-up []
  (rf/dispatch [::events/on-up]))

(defn on-move [e]
  (rf/dispatch [::events/on-move e]))

(defn rygb-string-input
  []
  (let [valid? (rf/subscribe [::subs/rygb-string-input-valid?])
        input (rf/subscribe [::subs/rygb-string-input])]
    [:div.rygb-string-input.center
     [:input.ux {:type "text"
                 :class (when-not @valid? "error")
                 :value @input
                 :on-change #(rf/dispatch [::events/rygb-input-change (-> % .-target .-value)])}]
     (when-not @valid? [:div.qmark "?"])]))

(defn tempest []
  (let [{:keys [w h]} @(rf/subscribe [::subs/window])]
    [:div "hi"]))

(defn add-events [events]
  (doseq [[e handler] events]
    (js/window.addEventListener e handler)))

(defn remove-events [events]
  (doseq [[e handler] events]
    (js/window.removeEventListener e handler)))

(defn transform-css [m]
  (let [tx (reduce-kv (fn [acc k v]
                        (conj acc (str (name k) "(" v ")") )) [] m)]
    (string/join " " tx)))

(defn ballin [ace k]
  (let [hues {:hue1 (-> ace :ks first) :hue2 (-> ace :ks second)}
        hues-invert (map-invert hues)
        visibility (if (contains? (into #{} (vals hues)) k) "visible" "hidden")
        transform-scale (when-let [scale (some-> ace :scale (get (k hues-invert)))]
                          {:transform (transform-css {:translate "-50%, -50%" :scale scale})})]
    (merge {:visibility visibility} transform-scale)))

(defn sva-slider [k position-sub color-sub rygb-map]
  [:div.slider-wrap.flex
   [:div.slider.flex.wireframe {:class k}
    [:div.track {:class  k :id  k :style @color-sub}
     [:div.ball-control {:class k :style @position-sub}
      [:div.label.ux {:class k}
       (str (name k) (-> @rygb-map k (* 100) js/Math.round))]]]]])

(defn menu [class]
  (let [blur (when @(rf/subscribe [::subs/info?]) "blur")]
    [:div.menu {:class [class blur]}
     [:div.about {:on-click #(rf/dispatch [::events/show-info])} "About"]
     [:div.title "~ RYGB ~"]
     [:div.source
      [:a {:href "https://github.com/paintparty/rygb-js"
           :target "_blank"}
       "Source"]]]))

(defn info-panel []
  [:div.info
   {:style {:display (when-not @(rf/subscribe [::subs/info?]) "none")}}
   [:div.content
    [:p "RYGB color notation is a syntactical abstraction over the HSV color model. Specifically, it provides an intuitive and analogous approach to expressing hue by way of additive, proportional mixing of adjacent primary colors."]
    [:p
     "The RYGB chromatic model is based on the"
     [:a {:href "https://en.wikipedia.org/wiki/Opponent_process" :target "_blank"}
      " opponent process color theory"]
     ", first theorized by physiologist Ewald Hering in 1892."]
    [:p "Detailed information on RYGB usage and implementation can be found at the respective repos for "
     [:a {:href "https://github.com/paintparty/rygb-js" :target "_blank"} "JavaScript"]
     " and "
     [:a {:href "https://github.com/paintparty/rygb-cljc" :target "_blank"} "Clojure(Script)"]]]
   [:div.close-wrap
    {:on-click #(rf/dispatch [::events/close-info])}
    [:div.close
     [:div.rg]
     [:div.yb]]
    ]
   [:p.copyright
    "© 2019 " (.fromCharCode js/String 160)
    [:a {:href "https://github.com/paintparty/rygb-demo" :target "_blank"}
    [:span.amp "@"] "paintparty" ]]])

(defn main-panel []
  (let [on-resize (fn [e]
                    (rf/dispatch [::events/resize]))
        events {"resize" on-resize
                "mousedown" on-down
                "touchstart" on-down
                "mousemove" on-move
                "touchmove" on-move
                "mouseup" on-up
                "touchend" on-up}]
    (r/create-class
     {:component-did-mount #(do (rf/dispatch [::events/resize])
                              (add-events events))
      :component-will-unmount #(remove-events events)
      :reagent-render (fn []
                        (let [bgc (rf/subscribe [::subs/rygb->hex])
                              rygb-map (rf/subscribe [::subs/rygb-map])
                              blaster (rf/subscribe [::subs/blaster])
                              ace (rf/subscribe [::subs/ace])
                              window (rf/subscribe [::subs/window])
                              landscape? (= :landscape (:orientation @window))
                              s-control (rf/subscribe [::subs/s-control :s])
                              v-control (rf/subscribe [::subs/v-control :v])
                              s-track (rf/subscribe [::subs/s-track :s])
                              v-track (rf/subscribe [::subs/v-track :v])
                              tempest-sz (rf/subscribe [::subs/tempest])
                              blur (when @(rf/subscribe [::subs/info?]) "blur")]
                          [:div.app-wrap.wh-100.atl

                           [:div.horizontal.guide]
                           [:div.vertical.guide]

                           [info-panel]

                           (when landscape? [menu "landscape"])

                           [:div.controls.flex.center.wireframe
                            {:class [(-> @window :orientation) blur]}

                            (when-not landscape? [menu "portrait"])

                            ;saturation value
                            [:div.sv-controls-wrap.flex.flex-auto.wireframe
                             {:style {:flex 1}}
                             [:div.sv-controls.flex.wireframe
                              [sva-slider :v v-control v-track rygb-map]
                              [:div.spacer.wireframe-red]
                              [sva-slider :s s-control s-track rygb-map]]]

                            ;hue selection
                            [:div.tempest.wireframe
                             (let [sz (js/Math.round @tempest-sz)
                                   sz-px (util/px sz)
                                   inner-sz-px (util/px (- sz (* 2 ui-config/track-width)))]
                               [:div#circle.diamond.center.bg
                                {:style {:width sz-px :height sz-px}}
                                [:div.inner
                                 {:style {:width inner-sz-px :height inner-sz-px}}]])
                             [:div#circle.diamond.center
                              [:div#blaster-track.center
                               {:style
                                (let [sz "99%"]
                                  {:width sz
                                   :height sz})}
                               [:div.blaster.wh-33 {:style @blaster}
                                [:div.balls.center
                                 [:div.center.ball-control]
                                 [:div.center.r {:class (if landscape? :outside :inside)
                                                 :style (ballin @ace :r)}]
                                 [:div.center.y {:class (if landscape? :inside :outside)
                                                 :style (ballin @ace :y)}]
                                 [:div.center.g {:class (if landscape? :outside :inside)
                                                 :style (ballin @ace :g)}]
                                 [:div.center.b {:class (if landscape? :inside :outside)
                                                 :style (ballin @ace :b)}]]]]]
                             [:div#magic-circle.hue.center]
                             [rygb-string-input]]

                            [:div.color.flex.flex-auto.wireframe.bg-b-a10
                             [:div.sample.flex
                              {:style {:background @bgc}}
                              [:div.color-chips.flex
                               (for [c [:k :g33 :g66 :w]]
                                 ^{:key c} [:div.flex {:class c}])]
                              [:div.color-chips.flex
                               (for [c [:b :g :y :r]]
                                 ^{:key c} [:div.flex {:class c}])]]]]]))})))
