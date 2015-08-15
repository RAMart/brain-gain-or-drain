(ns brain-gain-or-drain.entity
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [g2d :refer :all]
                      [ui :refer :all :exclude [align]]
                      [utils :as u]]
            [brain-gain-or-drain.utils :refer :all])
  (:import com.badlogic.gdx.scenes.scene2d.utils.Align
           com.badlogic.gdx.graphics.Color))

(defn bound?
  [entity]
  (contains? entity ::binding-relevant))

(defn when-entity
  [pred f entities]
  (reduce (fn [new-entities entity]
            (if-let [new-entity (if (pred entity) (f entity) entity)]
              (if (sequential? new-entity)
                (apply conj new-entities new-entity)
                (conj new-entities new-entity))
              new-entities))
          []
          entities))

(defn audio-resource-key
  [resource]
  (cond
   (music? resource) :music
   (sound? resource) :sound
   (vector-of? sound? resource) :sound-vector))

(defn graphics-resource-key
  [resource]
  (cond
   (image? resource) :image
   (texture? resource) :texture
   (array-of? texture? resource) :texture-array
   (string? resource) :label))

(defn resource-type-key
  [resource]
  (or
   (graphics-resource-key resource)
   (audio-resource-key resource)
   :unknown))

(defn- align
  [align-key]
  (-> Align
      (.getField (u/key->camel align-key))
      (.get Align)))

(defn- color
  [color-key]
  (-> Color
      (.getField (u/key->upper color-key))
      (.get Color)))

(defn- bind-graphics
  [entity graphics width height]
  (let [entity-with-defaults (merge graphics
                                    {:x 0 :y 0 :z 0
                                     :width width
                                     :height height}
                                    entity)]
    (merge entity-with-defaults
           {::binding-relevant (dissoc entity-with-defaults
                                       ::sync :object :x :y :width :height)})))

(defn- bind-texture
  [entity source-texture]
  (let [sprite (texture source-texture)]
    (bind-graphics entity
                   sprite
                   (texture! sprite :get-region-width)
                   (texture! sprite :get-region-height))))

(defn- bind-image
  [entity source-image]
  (bind-graphics entity
                 source-image
                 (image! source-image :get-image-width)
                 (image! source-image :get-image-height)))

(defn- update-text-and-bounds!
  [label-entity text]
  (label! label-entity :set-text text)
  (let [label-bounds (label! label-entity :get-text-bounds)
        text-based-properties {:text text
                               :width (. label-bounds width)
                               :height (. label-bounds height)}]
    (-> label-entity
        (update-in [::binding-relevant]
                   merge text-based-properties)
        (merge text-based-properties))))

(defn- bind-label
  [entity text color-key align-key]
  (let [entity-with-defaults (merge (label text
                                           (color color-key)
                                           :set-alignment (align align-key))
                                    {:x 0 :y 0
                                     :text text
                                     :color color-key
                                     :align align-key}
                                    entity)]
    (-> entity-with-defaults
        (merge {::binding-relevant (dissoc entity-with-defaults
                                           ::sync :object :x :y)
                ::sync {:text update-text-and-bounds!}})
        (update-text-and-bounds! text))))

(defmulti bind-entity
  (fn [entity resources]
    (if (bound? entity)
      :bound-entity
      (resource-type-key ((:type entity) resources)))))

(defn rebind-entity
  [entity resources]
  (let [bound-to (::binding-relevant entity)]
    (if (not= bound-to
              (select-keys entity
                           (keys bound-to)))
      (bind-entity (dissoc entity ::binding-relevant ::sync :object)
                   resources)
      entity)))

(defn sync-entity
  [entity]
  (if-let [syncs (::sync entity)]
    (doall
     (reduce (fn [sync-entity sync-key]
               (let [bound-value (get-in sync-entity [::binding-relevant sync-key])
                     current-value (sync-key sync-entity)]
                 (if (not= current-value bound-value)
                   ((sync-key syncs) sync-entity current-value)
                   sync-entity)))
             entity
             (keys syncs)))
    entity))

(defmethod bind-entity :bound-entity
  [entity resources]
  (-> entity
      (sync-entity)
      (rebind-entity resources)))

(defmethod bind-entity :texture
  [entity resources]
  (bind-texture entity
                ((:type entity) resources)))

(defmethod bind-entity :texture-array
  [entity resources]
  (bind-texture entity
                (aget ((:type entity) resources)
                      0
                      (-> entity :z (or 0)))))

(defmethod bind-entity :image
  [entity resources]
  (bind-image entity
              ((:type entity) resources)))

(defmethod bind-entity :label
  [entity resources]
  (bind-label entity
              (-> entity :text (or ((:type entity) resources)))
              (-> entity :color (or :white))
              (-> entity :align (or :left))))

(defmethod bind-entity :music
  [entity resources]
  (merge {:x 0  ;; TODO: Sound position
          :y 0  ;; TODO: Sound position
          :z 0  ;; TODO: Loudness
         }
         entity
         {::binding-relevant (dissoc entity :object)
          ;; TODO ::sync {... }
         }))

(defmethod bind-entity :sound
  [entity resources]
  (merge {:x 0  ;; TODO: Sound position
          :y 0  ;; TODO: Sound position
          :z 0  ;; TODO: Loudness
         }
         entity
         {::binding-relevant (dissoc entity :object)
          ;; TODO ::sync {... }
         }))

(defmethod bind-entity :sound-vector
  [entity resources]
  (merge {:x 0  ;; TODO: Sound position
          :y 0  ;; TODO: Sound position
          :z 0  ;; TODO: Loudness
         }
         entity
         {::binding-relevant (dissoc entity :object)
          ;; TODO ::sync {:effect-index ... }
         }))

(defn bind-entities
  [resources entities]
  (mapv #(bind-entity % resources) entities))
