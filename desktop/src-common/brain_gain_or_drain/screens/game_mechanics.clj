(in-ns 'brain-gain-or-drain.screens.game)

(defn- move-back-in-time
  [screen entities destination-reached-fn]
  (let [delta-game-time 1/2
        delta-frames (int (* delta-game-time (game :fps)))]
    (if (< delta-frames (count (:timeline screen)))
      (destination-reached-fn delta-game-time
                              (rewind! screen delta-frames))
      entities)))

(defmulti move-entity
  (fn [_ _ entity] (:type entity)))

(defmethod move-entity :logo
  [delta-time _ logo]
  (let [delta-x (-> (* 4 60 delta-time)
                    (/ (inc (:z logo))))]
    (assoc logo :x (-> logo :x (- delta-x)))))

(defmethod move-entity :player
  [delta-time move-direction player]
  (let [delta-y (* 6 60 delta-time)]
    (case move-direction
      :move-up (assoc player :y (-> player :y (+ delta-y)))
      :move-down (assoc player :y (-> player :y (- delta-y)))
      player)))

(defmethod move-entity :default
  [delta-time game-input entity]
  (println "WARNING: Don't know how to handle (move-entity" delta-time game-input entity ")")
  entity)

(defmulti move-entities
  (fn [_ game-input _]
    (case game-input
      :move-back-in-time :back-in-time
      :travel-back-in-time :time-traveling
      :in-space)))

(defmethod move-entities :back-in-time
  [screen game-input entities]
  (move-back-in-time screen entities
                     (fn [delta-game-time past-entities]
                       past-entities)))

(defmethod move-entities :time-traveling
  [screen game-input entities]
  (move-back-in-time screen entities
                     (fn [_ past-entities]
                       (concat (remove player? past-entities)
                               (filter player? entities)))))

(defmethod move-entities :in-space
  [screen-or-delta-time move-direction entities]
  (let [delta-time (if (map? screen-or-delta-time)
                     (:delta-time screen-or-delta-time)
                     screen-or-delta-time)]
    (mapv #(move-entity delta-time move-direction %) entities)))
