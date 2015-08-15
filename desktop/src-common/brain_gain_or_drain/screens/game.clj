(ns brain-gain-or-drain.screens.game
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all :exclude [audio!]]
                      [g2d :refer :all]
                      [utils :as u]]
            [brain-gain-or-drain [entity :refer :all]
                                 [domain :refer :all]
                                 [input :refer :all]]))

(load "game_mechanics")

(defn recreate-entity
  [entity width height]
  (assoc entity
         :x width
         :y (rand-int height)))

(defn sprite-generator
  [template max-x max-y]
  (repeatedly (fn []
                (merge template
                       {:x (rand-int max-x)
                        :y (rand-int max-y)}))))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :resources
             {
              :player (texture "brain.png")
              :logo (texture! (texture "clojure-logos.png") :split 64 64)
             })
    [(take 20 (sprite-generator {:type :logo :z 2} (width screen) (height screen)))
     (take 15 (sprite-generator {:type :logo :z 1} (width screen) (height screen)))
     (take 10 (sprite-generator {:type :logo :z 0} (width screen) (height screen)))
     {:type :player :x 32 :y 100 :z 0}])

  :on-render
  (fn [screen entities]
    (clear!)
    (let [entities (bind-entities (:resources screen) entities)
          players (filter player? entities)]
      (letfn [(recreate-entity-on-screen [entity]
                (recreate-entity entity (width screen) (height screen)))
              (eaten-by-players? [entity]
                (some #([player] (eaten-by? player entity)) players))]
        (->> entities
             (move-entities (:delta-time screen) (game-input!))
             (when-entity gone? recreate-entity-on-screen)
             (when-entity eaten-by-player? recreate-entity-on-screen)
             (render! screen)))))

  :on-resize
  (fn [screen entities]
    (width! screen 800)))
