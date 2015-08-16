(ns brain-gain-or-drain.renderer
  (:refer-clojure :exclude [bound?])
  (:require [play-clj.core :refer :all :exclude [audio!]]
            [brain-gain-or-drain [audio :refer :all]
                                 [entity :refer :all]
                                 [utils :refer :all]]))

(defn stop-unused-music!
  [screen entities]
  (let [used-resources (set (map #(:type %) entities))
        unused-music (for [[key resource] (screen :resources)
                           :when (and (music? resource)
                                      (not (contains? used-resources key)))]
                       resource)]
    (doall (map #(audio! % :stop) unused-music))))

(defn render-audio!
  [screen entities]
  (stop-unused-music! screen entities)
  (remove (fn [entity]
            (let [resource ((:type entity) (screen :resources))]
              (when-let [audio-type (audio-resource-key resource)]
                (case audio-type
                  :music (audio! resource :play)
                  :sound (do (audio! resource :play) :remove)
                  :sound-vector (do (audio! resource :play (:effect-index entity)) :remove)))))
          entities))

(defn render-all!
  [screen entities]
  (->> entities
       (render-audio! screen)
       (render! screen)))
