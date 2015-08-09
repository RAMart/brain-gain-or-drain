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

(defn eatable?
  [entity]
  (and (= (:type entity) :logo)
       (= (:z entity) 0)))

(defn hotspot
  [entity]
  {:x (+ (:x entity)
         (/ (:width entity) 2))
   :y (+ (:y entity)
         (/ (:height entity) 2))})

(defn collide?
  [entity1 entity2]
  (when (= (:z entity1) (:z entity2))
    (let [hotspot1 (hotspot entity1)
          hotspot2 (hotspot entity2)
          distance-x (abs (- (:x hotspot1) (:x hotspot2)))
          distance-y (abs (- (:y hotspot1) (:y hotspot2)))]
      (and (< distance-x 16)
           (< distance-y 16)))))

(defn eaten-by?
  [predator prey]
  (and (eatable? prey)
       (collide? predator prey)))
