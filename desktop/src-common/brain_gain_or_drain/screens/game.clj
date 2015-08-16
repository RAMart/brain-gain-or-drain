(ns brain-gain-or-drain.screens.game
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all :exclude [audio!]]
                      [g2d :refer :all]
                      [utils :as u]]
            [brain-gain-or-drain [entity :refer :all]
                                 [domain :refer :all]
                                 [audio :refer :all]
                                 [input :refer :all]
                                 [renderer :refer :all]
                                 [utils :refer :all]]
            [brain-gain-or-drain.screens.utils :refer :all]))

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

(defn sound-effect
  [effect-index]
  {:type :sound-effects
   :effect-index effect-index})

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :paused false
             :timeline []
             :resources
             {
              :player (texture "brain.png")
              :logo (texture! (texture "clojure-logos.png") :split 64 64)
              :sound-effects [(sound "huhuhuha.mp3")
                              (sound "mmmh.mp3")
                              (sound "njamm.mp3")
                              (sound "yummy.mp3")
                              (sound "yumyum.mp3")]
              :background-music (music "03 - The Clou - Xmas.mp3")
             })
    [(take 20 (sprite-generator {:type :logo :z 2} (width screen) (height screen)))
     (take 15 (sprite-generator {:type :logo :z 1} (width screen) (height screen)))
     (take 10 (sprite-generator {:type :logo :z 0} (width screen) (height screen)))
     {:type :player :x 32 :y 100 :z 0}
     {:type :background-music}])

  :on-render
  (fn [screen entities]
    (when-not (screen :paused)
      (let [screen (limit-timetravel! screen)
            entities (bind-entities (:resources screen) entities)
            players (filter player? entities)
            logo-gone? (every-pred logo? gone?)]
        (letfn [(recreate-entity-on-screen [entity]
                  (recreate-entity entity
                                   (width screen)
                                   (height screen)))
                (add-player-sound [entity]
                  (vector entity
                          (sound-effect (-> (get-in screen [:resources :sound-effects])
                                            (count)
                                            (rand-int)))))
                (eaten-by-players? [entity]
                  (some (fn [player] (eaten-by? player entity)) players))]
          (clear!)
          (->> entities
               (move-entities screen (game-input!))
               (when-entity logo-gone? recreate-entity-on-screen)
               (when-entity eaten-by-players? (comp add-player-sound
                                                    recreate-entity-on-screen))
               (render-all! screen))))))

  :on-resize
  (fn [screen entities]
    (width! screen 800))

  :on-resume
  (fn [screen entities]
    (update! screen :paused false)
    (audio! screen :resume)
    entities)

  :on-pause
  (fn [screen entities]
    (update! screen :paused true)
    (audio! screen :pause)
   entities)

  :on-hide
  (fn [screen entities]
    (audio! screen :dispose)))
