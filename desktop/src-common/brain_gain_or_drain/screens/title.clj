(ns brain-gain-or-drain.screens.title
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all]
                      [ui :refer :all]]
            [brain-gain-or-drain [entity :refer :all]
                                 [utils :refer :all]]
            [brain-gain-or-drain.screens [game :refer [game-screen]]
                                         [overlay :refer [overlay-screen]]
                                         [utils :refer :all]]))

(defn start-game!
  [screen entities]
  (show-screen! game-screen overlay-screen))

(defscreen title-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :resources
             {
              :title (image "title.png")
             })
    [{:type :title :full-screen? true}])

  :on-render
  (fn [screen entities]
    (->> entities
         (bind-entities (:resources screen))
         (render! screen)))

  :on-resize
  (fn [screen entities]
    (width! screen (game :width))
    (mapv (fn [entity]
            (if (:full-screen? entity)
              (assoc entity
                     :width (game :width)
                     :height (game :height))
              entity))
          entities))

  :on-key-down start-game!

  :on-tap start-game!)
