(ns brain-gain-or-drain.repl
  (:refer-clojure :exclude [bound?])
  (:require [clojure [pprint :refer :all]
                     [repl :refer :all]]
            [play-clj [core :refer :all]
                      [repl :refer :all]]
            [brain-gain-or-drain [entity :refer :all]
                                 [domain :refer :all]
                                 [utils :refer :all]
                                 [core :refer :all]]
            [brain-gain-or-drain.screens [error :refer [error-screen]]
                                         [title :refer [title-screen]]
                                         [game :refer :all]]))

(defn- purify-entities
  [entities]
  (mapv #(dissoc % :object
                   :brain-gain-or-drain.entity/binding-relevant
                   :brain-gain-or-drain.entity/sync)
        entities))

(defn show-screen!
  [& screens]
  (on-gl (apply set-screen! brain-gain-or-drain-game screens)))

(defn restart-game!
  []
  (show-screen! title-screen))

(defn pprint-entities
  ([screen]
   (pprint-entities screen identity))
  ([screen filter-fn]
   (->> (e filter-fn screen)
        (purify-entities)
        (pprint))))

(defn add-entity!
  [screen entity]
  (purify-entities (swap! (:entities screen) conj entity)))

(defn add-entities!
  [screen entities]
  (purify-entities (swap! (:entities screen) #(apply conj % entities))))

(defn set-entities!
  [screen entities]
  (purify-entities (swap! (:entities screen) (fn [_] entities))))

(defn clear-entities!
  [screen]
  (set-entities! screen []))

(defn- modify-entity
  [entity filter-fn & key-values]
  (if (filter-fn entity)
    (apply assoc
           entity
           key-values)
    entity))

(defn- modify-entities
  [entities filter-fn & key-values]
  (mapv #(apply modify-entity
                %
                filter-fn
                key-values)
        entities))

(defn modify-entities!
  [screen filter-fn & key-values]
  (purify-entities (swap! (:entities screen)
                          #(apply modify-entities
                                  %
                                  filter-fn
                                  key-values))))
