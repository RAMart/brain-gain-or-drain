(ns brain-gain-or-drain.screens.error
  (:require [play-clj [core :refer :all]
                      [ui :refer :all]]))

(defn- layout
  [screen-width screen-height entities]
  (for [entity entities]
    (cond
     (label? entity) (let [bounds (label! entity :get-text-bounds)
                           width (. bounds width)
                           height (. bounds height)]
                       (assoc entity
                              :x (-> screen-width
                                     (- width)
                                     (/ 2))
                              :y (-> screen-height
                                     (- height)
                                     (/ 2))))
     :else entity)))

(defscreen error-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage))
    (label "I'm sorry Dave,\nI'm afraid I can't do that."
           (color :white)
           :set-alignment (align :center)))

  :on-render
  (fn [screen entities]
    (clear! 0.75 0 0 1)
    (->> entities
         (layout (stage! screen :get-width)
                 (stage! screen :get-height))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (width! screen (-> (game :width) (/ 2)))))
