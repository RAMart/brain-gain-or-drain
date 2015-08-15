(in-ns 'brain-gain-or-drain.screens.game)

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

(defn move-entities
  [delta-time game-input entities]
  (mapv #(move-entity delta-time game-input %) entities))
