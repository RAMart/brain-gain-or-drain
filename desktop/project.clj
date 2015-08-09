(defproject brain-gain-or-drain "0.0.1-SNAPSHOT"
  :description "Simple game used on the \"Game development with Clojure\" talk at FrOSCon 2015"
  :url "https://github.com/RAMart/brain-gain-or-drain"
  :license {:name "The MIT License (MIT)"
            :url "https://github.com/RAMart/brain-gain-or-drain/blob/master/LICENSE"}

  :dependencies [[com.badlogicgames.gdx/gdx "1.5.5"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.5.5"]
                 [com.badlogicgames.gdx/gdx-box2d "1.5.5"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.5.5"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.5.5"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.5.5"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.5.5"
                  :classifier "natives-desktop"]
                 [org.clojure/clojure "1.7.0"]
                 [play-clj "0.4.6"]]

  :jvm-opts                        ; Inspired by: Overtone by Sam Aaron
    ["-Xms512m" "-Xmx1g"           ; Minimum and maximum sizes of the heap
     "-XX:+UseParNewGC"            ; Use the new parallel GC in conjunction with
     "-XX:+UseConcMarkSweepGC"     ; the concurrent garbage collector
     "-XX:+CMSConcurrentMTEnabled" ; Enable multi-threaded concurrent gc work (ParNewGC)
     "-XX:MaxGCPauseMillis=20"     ; Specify a target of 20ms for max gc pauses
     "-XX:+CMSIncrementalMode"     ; Do many small GC cycles to minimize pauses
     "-XX:MaxNewSize=257m"         ; Specify the max and min size of the new
     "-XX:NewSize=256m"            ; generation to be small
     "-XX:+UseTLAB"                ; Uses thread-local object allocation blocks. This
                                   ; improves concurrency by reducing contention on
                                   ; the shared heap lock.
     "-XX:MaxTenuringThreshold=0"] ; Makes the full NewSize available to every NewGC
                                   ; cycle, and reduces the pause time by not
                                   ; evaluating tenured objects. Technically, this
                                   ; setting promotes all live objects to the older
                                   ; generation, rather than copying them.

  :source-paths ["src" "src-common"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [brain-gain-or-drain.core.desktop-launcher]
  :main brain-gain-or-drain.core.desktop-launcher)
