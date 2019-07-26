(ns rygb-demo.css
  (:require
   [rygb-demo.ui-config :as ui-config :refer [control-vmin diagonal square-max square-min square-max-portrait square-min-portrait]]
   [garden.def :refer [defstyles defcssfn defkeyframes]]
   [garden.stylesheet :refer [at-media at-keyframes]]
   [garden.core :refer [style]]
   [garden.units :refer [px percent deg vmin vmax rem s]]
   [rygb.core :as rygb :refer [rygb->hex rygb->rgba-css rygb->rgb-css]]))

(defcssfn conic-gradient)
(defcssfn linear-gradient)
(defcssfn translate)
(defcssfn translateX)
(defcssfn translateY)
(defcssfn rotate)


(def conic-points
  [["ry" 0]
   ["y"  12.5]
   ["g"  37.5]
   ["gb" 50]
   ["b"  62.5]
   ["br" 75]
   ["r"  87.5]
   ["ry" 100]])

(def conic-gradient-value
  (map (fn [[v pos]]
           [(rygb->rgb-css v)
            (percent pos)])
       conic-points))

(def trans-center
  (translate (percent -50) (percent -50)))

(def p100 (percent 100))
(def p50 (percent 50))
(def p33 (percent 33))

(def sv-ball-control-offset
 (* 0.5 (- ui-config/track-width ui-config/ball-control-diameter)))

(defstyles screen
  ["*" "*:before" "*:after"
   {:border 0
    :margin 0
    :padding 0
    :box-sizing 'border-box
    :outline 0
    :-webkit-user-select 'none
    :-moz-user-select 'none
    :-ms-user-select 'none
    :user-select 'none}]

  [:html :body
   {:color 'black
    :height p100
    :overflow 'hidden
    :position 'fixed
    :top 0
    :left 0
    :right 0
    :background ui-config/bgc}]

  [:a :.about
   {:color 'white
    :text-decoration 'none
    :cursor 'pointer}
   [:&:hover {:text-decoration 'underline}]]

  (at-keyframes :color-rotate
                [:0% {:color (rygb->rgb-css "y")}]
                [:12.5% {:color (rygb->rgb-css "yg-s80")}]
                [:25% {:color (rygb->rgb-css "g-s80")}]
                [:37.5% {:color (rygb->rgb-css "gb-s60")}]
                [:50% {:color (rygb->rgb-css "b-s60")}]
                [:62.5% {:color (rygb->rgb-css "br-s60")}]
                [:75% {:color "r-s70"}]
                [:87.5% {:color "ry-s70"}])
  [:.info
   {:font-family ["Gentium Basic" 'serif]
    :font-weight 400
    :line-height (rem 1.7)
    :font-size (rem 1.2)
    :color 'white
    :position 'fixed
    :top 0
    :left 0
    :right 0
    :bottom 0
    :width (percent 100)
    :height (percent 100)
    :background (rygb->rgba-css "v0-a69")
    :z-index 1000
    :padding (px 50)
    :display 'flex
    :flex-direction 'column
    :justify-content 'center}
   (at-media {:max-width (px 450)}
             [:& {:padding (px 20)
                  :font-size (rem 1)
                  :line-height (rem 1.5)}])
   [:.content {:max-width (px 400)
               :margin-top (px -50)}]
   [:p {:margin [[(px 20) (px 20)]]}]
   [:a
    {:color 'yellow
     :text-decoration 'underline
     :animation [[:color-rotate (s 16) :infinite :linear]]}]
   [:.close-wrap
    {:position 'fixed
     :top 0
     :right 0
     :width (px 60)
     :height (px 60)
     :padding (px 20)
     :cursor 'pointer}
    [:.close
     {:width (percent 100)
      :height (percent 100)
      :position 'relative}
     [:.rg
      {:background 'white
       :height (px 2)
       :width (str "calc(100% * " diagonal ")")
       :position 'absolute
       :top (percent 50)
       :left (percent 50)
       :transform "translate(-50%, 50%) rotate(45deg)"}]
     [:.yb
      {:background 'white
       :height (px 2)
       :width (str "calc(100% * " diagonal ")")
       :position 'absolute
       :top (percent 50)
       :left (percent 50)
       :transform "translate(-50%, 50%) rotate(-45deg)"}]]]
   [:.copyright {:padding [[0 0 0 (px 50)]]
                 :position 'absolute
                 :bottom 0
                 :left 0
                 :font-size (rem 0.8)}
    [:.amp {:font-size (rem 0.4)}]]]

  [:.menu
   {:display 'flex
    :justify-content 'space-between
    :padding (px 18)
    :color 'white
    :font-weight 700
    :font-size (rem 0.7)
    :letter-spacing (rem 0.03)
    :text-transform 'uppercase
    :font-family ["Gentium Basic" 'serif]}

   [:.title {:margin-right (px -8)}]
   [:&.landscape
    {:padding (px 12)
     :width (px 222)
     :display 'flex
     :position 'fixed
     :bottom 0
     :left 0}
    [:.title {:order 1}]
    [:.about {:order 2}]
    [:.source {:order 3}]]
   [:&.portrait
    {:flex [[0 1 (vmax 10)]]
     :order 1
     :width (vmin 100)
     :margin-left "calc((0px - (100vmin - 100%)) / 2)"}]]

  [:.controls
   {:justify-content 'center}
   [:&.portrait
    {:width square-min-portrait
     :max-width square-max-portrait
     :flex-direction 'column
     :height p100}
    [:.sv-controls-wrap
     {:flex-direction 'column
      :flex [0 1 (percent 0)]
      :order 3
      :justify-content 'flex-start}
     [:.sv-controls
      {:margin-bottom (px 15)
       :flex-direction 'column-reverse}
      (at-media {:max-height (px 600)}
                [:div {:max-height (vmax 3)}])
      [:.spacer {:flex [[0 0 (vmax 3)]]}]
      [:.slider-wrap {:flex-direction 'column}
       [:.slider {:margin [[0 'auto]]
                  :max-height (px 20)
                  :flex-direction 'row}]]
      [:.track {:margin [[0 'auto]]
                :height (px ui-config/track-width)}
       [:.ball-control
        {:transform (str "translateY(" sv-ball-control-offset "px)")
         :left 0}
        [:.label
         {:top (px -16)
          :right (percent -50)}]]]]]
    [:.tempest
     {:height square-min-portrait
      :max-height square-max-portrait
      :margin [[(px 28) 0]]
      :order 2}]
    [:.color
     {:justify-content 'center
      :order 3}
     [:.sample
      {:justify-content 'space-between
       :flex [[0 0 (vmin 100)]]}]
     [:.color-chips {:flex-direction 'column}]]]

   [:&.landscape
    {:height square-min
     :max-height square-max
     :width p100}
    [:.sv-controls-wrap
     [:.sv-controls
      {:margin-right (px -25)}
      (at-media {:max-height (px 500)}
                [:& {:flex-basis (vmin (* diagonal control-vmin 0.4 100))}])
      [:.spacer {:flex [[0 0 (vmax 5)]]
                 :max-width (px 33)}]
      [:.slider {:margin [['auto 0]]
                 :max-width (px 20)
                 :flex-direction 'column}]
      [:.track {:margin [['auto 0]]
                :width (px ui-config/track-width)}]
      [:.ball-control
       {:transform (str "translateX(" sv-ball-control-offset "px)")
        :left 0}]]]
    [:.tempest
     {:width square-min
      :max-width square-max
      :margin [[0 (px 58)]]}
     (at-media [{:max-width (px 700)} {:max-height (px 500)}]
               [:& {:margin [[0 (px 38)]]}])]
    [:.color
     {:flex-direction 'column
      :justify-content 'center}
     [:.sample
      {:justify-content 'space-between
       :flex [[0 0 (vmin 100)]]
       :flex-direction 'column}]]]]

  [:.color
   [:.color-chips
    {:flex [[0 0 (px (inc ui-config/track-width))]]}]]

  [:.tempest
   {:height ui-config/square-min
    :max-height ui-config/square-max
    :position 'relative}]

  [:.sv-controls-wrap
   {:justify-content 'flex-end}
   [:.sv-controls
    {:justify-content 'flex-end
     :flex-basis (vmin (* diagonal control-vmin 0.33 100))}
    [:.slider-wrap
     [:.slider
      {:height p100
       :width p100
       :cursor 'pointer
       :position 'relative
       :justify-content 'center
       :align-items 'center}]
     [:.track
      {:flex [[0 0 (percent 69)]]
       :position 'relative}]
     [:.ball-control
      [:.label {:position 'absolute
                :top 0
                :right (percent 130)
                :line-height (px ui-config/ball-control-diameter)}]]]]]

  [:.sv-controls :.color
   {:flex-basis (px 150)
    :justify-content 'space-between}
   [:div {:flex 'auto
          :justify-content 'center}]]

  ; global
  [:.ball-control
   {:width (px ui-config/ball-control-diameter)
    :height (px ui-config/ball-control-diameter)
    :border-radius p50
    :background 'white
    :position 'absolute
    :box-shadow [[0 (px 2) (px 5) (rygb->rgba-css "v0-a44")]]}]

  [:#circle
   {:width (percent 70.5) :height (percent 70.5)}
   [:&.bg
    {:background (apply conic-gradient conic-gradient-value)}]
   [:&.diamond
    {:transform [[trans-center (rotate (deg 45))]]}
    [:.inner
     (let [offset (ui-config/px ui-config/track-width)]
       {:background ui-config/bgc
        :position 'absolute
        :top offset
        :left offset})]]]

  [:#blaster-track
   {:border 'none #_{:width (px 1) :style 'solid :color 'white}}]

  [:.blaster {:cursor 'pointer
              :transform [[trans-center (rotate (deg 315))]]
              :position 'absolute}
   [:.balls {:width p33
             :height p100}
    [:.r :.y :.g :.b
     {:position 'absolute
      :width p100
      :height p33
      :border-radius p50
      :border [[(percent 5) 'solid 'transparent]]}
     [:&.ball-control]
     [:&.outside {:top (percent 16.5)}]
     [:&.inside {:top (percent 82.5)}]]]]

  [:#magic-circle
   {:cursor 'pointer
    :border-radius p50
    :width p100
    :height p100}]

  [:.ux {:font-family "Source Code Pro"
         :font-size (rem 1.2)
         :font-weight 400
         :color (rygb->rgb-css "v95")}
   (at-media [{:max-width (px 950)} {:max-height (px 700)}]
             [:& {:font-size (rem 1.2)}])
   (at-media [{:max-width (px 800)} {:max-height (px 600)}]
             [:& {:font-size (rem 1.1)}])
   (at-media [{:max-width (px 650)} {:max-height (px 500)}]
             [:& {:font-size (rem 1.0)}])
   (at-media [{:max-width (px 500)} {:max-height (px 400)}]
             [:& {:font-size (rem 0.9)}])
   (at-media [{:max-width (px 400)} {:max-width (px 300)}]
             [:& {:font-size (rem 0.9)}])]

  [:.rygb-string-input
   {:width (percent 44)
    :z-index 999}
   (at-media [{:max-width (px 950)} {:max-height (px 700)}]
             [:& {:width (percent 46)}])
   (at-media [{:max-width (px 800)} {:max-height (px 600)}]
             [:& {:width (percent 48)}])
   (at-media [{:max-width (px 650)} {:max-height (px 500)}]
             [:& {:width (percent 50)}])
   (at-media [{:max-width (px 500)} {:max-height (px 400)}]
             [:& {:width (percent 52)}])
   (at-media [{:max-width (px 400)} {:max-width (px 300)}]
             [:& {:width (percent 54)}])
   [:input
    {:padding [[(rem 0.44) (rem 0.4) (rem 0.36)]]
     :border [[(px 1) 'solid (rygb->rgba-css "v100-a30")]]
     :background 'transparent
     :width p100}
    [:&.error {:border-color 'red
               :color 'red}]]
   [:.qmark {:position 'absolute
             :right (px -14)
             :top (px -13)
             :color 'red
             :font-family "Source Code Pro"}]]

  [:&.r {:background (rygb->rgb-css "r")}]
  [:&.y {:background (rygb->rgb-css "y")}]
  [:&.g {:background (rygb->rgb-css "g")}]
  [:&.b {:background (rygb->rgb-css "b")}]
  [:&.k {:background (rygb->rgb-css "v0")}]
  [:&.w {:background (rygb->rgb-css "v100")}]
  [:&.g33 {:background (rygb->rgb-css "v33")}]
  [:&.g66 {:background (rygb->rgb-css "v66")}]
  [:.blur {:filter "blur(10px)"}]
  [:.bg-shadow {:background (rygb->rgba-css "v50-a10")}]
  [:.bg-glow {:background (rygb->rgba-css "v100-a10")}]
  [:.bg-r-a10 {:background (rygb->rgba-css "r-a10")}]
  [:.bg-g-a10 {:background (rygb->rgba-css "g-a10")}]
  [:.bg-b-a10 {:background (rygb->rgba-css "b-a10")}]
  [:.b-r-a20 {:border [[(px 1) 'solid (rygb->rgba-css "r-a20")]]}]
  [:.wireframe {:box-shadow  nil #_[['inset 0 0 (px 1) (px 1) (rygb->rgba-css "v100-a20")]]
                :color (rygb->rgba-css "v50")}]
  #_[:.wireframe-red {:box-shadow [['inset 0 0 (px 10) (px 1) (rygb->rgba-css "g-a70")]]
                      :color (rygb->rgba-css "v50")}]
  [:.wireframe-dark {:box-shadow [['inset 0 0 (px 1) (px 1) (rygb->rgba-css "v50-a20")]]
                     :color (rygb->rgba-css "v50")}]
  [:.guide
   {:display 'none
    :position 'fixed
    :background (rygb->rgba-css "v50")
    :z-index 1000}]
  [:.horizontal
   {:width p100
    :height (px 1)
    :top p50}]
  [:.vertical
   {:height p100
    :width (px 1)
    :left p50}]
  [:.wh-100 {:width p100 :height p100}]
  [:.wh-33 {:width p33 :height p33}]
  [:.atl {:position 'absolute :top 0 :left 0}]
  [:.bg-black {:background 'black}]
  [:.bg-white {:background 'white}]
  [:.flex {:display 'flex}]
  [:.flex-auto {:flex 'auto}]
  [:.center
   {:position 'absolute
    :top p50
    :left p50
    :transform trans-center}])
