(ns brain-gain-or-drain.screens.overlay
  (:refer-clojure :exclude [bound? update])
  (:require  [play-clj.core :refer :all]
             [brain-gain-or-drain.entity :refer :all]))

(defn- update
  [entities]
  (for [entity entities]
    (case (:type entity)
      :fps-message (assoc entity
                          :text (str (game :fps) "fps"))
      entity)))

(defscreen overlay-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :resources
             {
              :fps-message "--fps"
             })
    [{:type :fps-message :color :white :x 5 :y 5}])

  :on-render
  (fn [screen entities]
    (->> entities
         (update)
         (bind-entities (:resources screen))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (width! screen (-> (game :width) (/ 2)))))
