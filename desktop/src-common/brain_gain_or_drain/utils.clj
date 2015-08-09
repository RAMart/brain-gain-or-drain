(ns brain-gain-or-drain.utils
  (:import (com.badlogic.gdx.audio Music Sound)))

(defn abs
  [n]
  (max n (- n)))

(defn music?
  [x]
  (instance? Music x))

(defn sound?
  [x]
  (instance? Sound x))

(defn array?
  [x]
  (.isArray (class x)))

(defn vector-of?
  [pred entities]
  (and (vector? entities)
       (every? pred entities)))

(defn array-of?
  [pred entities]
  (->> entities
       (tree-seq array? seq)
       (remove array?)
       (every? pred)))
