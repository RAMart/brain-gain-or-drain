(ns brain-gain-or-drain.domain
  (:require [brain-gain-or-drain.utils :refer :all]))

(defn gone?
  [entity]
  (< (:x entity) (- (:width entity))))

(defn player?
  [entity]
  (= (:type entity) :player))

(defn logo?
  [entity]
  (= (:type entity) :logo))
