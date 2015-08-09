(ns brain-gain-or-drain.screens.error
  (:refer-clojure :exclude [bound?])
  (:require [play-clj.core :refer :all]
            [brain-gain-or-drain.entity :refer :all]))

(defn- layout
  [screen-width screen-height entities]
  (for [entity entities]
    (case (:type entity)
      :error-message (assoc entity
                            :x (-> screen-width
                                   (- (:width entity))
                                   (/ 2))
                            :y (-> screen-height
                                   (- (:height entity))
                                   (/ 2)))
      entity)))

(defscreen error-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :resources
             {
              :error-message "I'm sorry Dave,\nI'm afraid I can't do that."
             })
    [{:type :error-message :color :white :align :center}])

  :on-render
  (fn [screen entities]
    (clear! 0.75 0 0 1)
    (->> entities
         (bind-entities (:resources screen))
         (layout (stage! screen :get-width)
                 (stage! screen :get-height))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (width! screen (-> (game :width) (/ 2)))))
