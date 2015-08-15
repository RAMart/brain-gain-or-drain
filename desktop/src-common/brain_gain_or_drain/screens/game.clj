(ns brain-gain-or-drain.screens.game
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all :exclude [audio!]]
                      [g2d :refer :all]
                      [utils :as u]]
            [brain-gain-or-drain [entity :refer :all]
                                 [domain :refer :all]
                                 [input :refer :all]]))

(defn move-logo
  [delta-time _ logo]
  (let [delta-x (-> (* 4 60 delta-time)
                    (/ (inc (:z logo))))]
    (assoc logo :x (-> logo :x (- delta-x)))))

(defn move-player
  [delta-time move-direction player]
  (let [delta-y (* 6 60 delta-time)]
    (case move-direction
      :move-up (assoc player :y (-> player :y (+ delta-y)))
      :move-down (assoc player :y (-> player :y (- delta-y)))
      player)))

(defn move-entity
  [delta-time game-input entity]
  (cond (logo? entity) (move-logo delta-time game-input entity)
        (player? entity) (move-player delta-time game-input entity)
        :else entity))

(defn move-entities
  [delta-time game-input entities]
  (mapv #(move-entity delta-time game-input %) entities))

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
     (take 10 (sprite-generator {:type :logo :z 0} (width screen) (height screen)))])

  :on-render
  (fn [screen entities]
    (clear!)
    (letfn [(recreate-entity-on-screen [entity]
              (recreate-entity entity (width screen) (height screen)))]
      (->> entities
           (bind-entities (:resources screen))
           (move-entities (:delta-time screen) (game-input!))
           (when-entity gone? recreate-entity-on-screen)
           (render! screen))))

  :on-resize
  (fn [screen entities]
    (width! screen 800)))
