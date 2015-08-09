(ns brain-gain-or-drain.domain
  (:require [brain-gain-or-drain.utils :refer :all]))

(defn gone?
  [entity]
  (< (:x entity) (- (:width entity))))
