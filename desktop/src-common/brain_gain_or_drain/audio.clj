(ns brain-gain-or-drain.audio
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all :exclude [audio!]]
                      [utils :as u]]
            [brain-gain-or-drain [entity :refer :all]
                                 [utils :refer :all]])
  (:import (com.badlogic.gdx.audio Music Sound)))

(defn audio-operation-dispatcher
  ([target operation]
   (audio-operation-dispatcher target operation nil))
  ([target operation parameter]
   (cond (music? target) :music
         (sound? target) :sound
         (and (vector? target) (integer? parameter)) :single-sound-effect
         (and (map? target) (:resources target)) :with-resources
         (or (coll? target) (sequential? target)) :multiple-targets)))

(defmulti audio! audio-operation-dispatcher)

(defmethod audio! :default
  ([target operation]
   (println "WARNING: Don't know how to handle audio!" target operation))
  ([target operation parameter]
   (println "WARNING: Don't know how to handle audio!" target operation parameter)))

(defmethod audio! :music
  ([song operation]
   (if (= operation :play)
     (when-not (music! song :is-playing)
       (music! song :set-looping true)
       (music! song :play))
     (when-not (= operation :resume)
       (-> Music
           (.getMethod (u/key->camel operation) nil)
           (.invoke song nil)))))
  ([song operation parameter]
   (when (and (= operation :rewind)
              (music! song :is-playing))
     (music! song :set-position (- (music! song :get-position) parameter)))))

(defmethod audio! :sound
  [effect operation]
  (-> Sound
      (.getMethod (u/key->camel operation) nil)
      (.invoke effect nil)))

(defmethod audio! :single-sound-effect
  [sound-set operation index]
  (audio! sound-set :stop)
  (audio! (get sound-set index) operation))

(defmethod audio! :multiple-targets
  ([targets operation]
   (doall (map #(audio! % operation) targets)))
  ([targets operation parameter]
   (doall (map #(audio! % operation parameter) targets))))

(defmethod audio! :with-resources
  [{:keys [resources]} operation]
  (audio! (filter audio-resource-key (vals resources))
          operation))
