(ns leiningen.cordova
  (:require [clojure.java.shell :refer [sh]]
            [leiningen.help :as lhelp]
            [leiningen.core.main :as lmain]
            [clojure.java.io :as io]))

(defn check-prereqs
  "Check for cordova binary in $PATH"
  []
  ;; Ensure can call npm/nodejs
  ;; Ensure can call cordova binary
  ;; Ensure can call git binary
  ;; Ensure can call java/javac

  ;; Ensure iOS SDK is installed (or)
  ;; Ensure android SDK is installed
  )

(defn- cordova-plugin-add
  [{:keys [cordova-binary app]} plugin]
  ;; cordova plugin add PLUGIN-SPEC
  [cordova-binary "plugin" "add" plugin :dir app])

(defn- add-plugin
  "Add the given cordova plugin to the project"
  [project options plugin]
  (apply sh (cordova-plugin-add options plugin)))

(defn- cordova-platform-add
  [{:keys [cordova-binary app]} platform]
  [cordova-binary "platform" "add" (name platform) :dir app])

(defn add-platform
  [project {:keys [cordova-binary app] :as options} platform]
  ;; cordova platform add PLATFORM
  (apply sh (cordova-platform-add options platform)))

(defn- cordova-create
  [{:keys [cordova-binary app package display-title]}]
  ;; cordova create APP PACKAGE DISPLAY-TITLE
  [cordova-binary "create" app package display-title])

(defn create
  "Create cordova project with specified web files."
  [project {:keys [platforms plugins] :as options} & args]

  (lmain/info (:out (apply sh (cordova-create options))))

  (dorun
   (for [p platforms]
     (lmain/info (:out (add-platform project options p)))))

  (dorun
   (for [p plugins]
     (lmain/info (:out (add-plugin project options p))))))

(defn- generate-sdk-env
  [sdk-path]
  (format "%s/platform-tools:%s/tools" sdk-path sdk-path))

(defn- cordova-build
  [{:keys [cordova-binary app android-sdk-path] :as options}]
  {:pre [cordova-binary]}
  [cordova-binary "build" :dir app :env (update-in (into {} (System/getenv)) ["PATH"] (fn [path] (str path ":" (generate-sdk-env android-sdk-path))))])

(defn build-all
  [project options & args]
  (apply sh (cordova-build options)))

(defn copy-tree
  [project {:keys [resource-root app] :as options} & args]
  ["rsync" "-avz" resource-root (str (io/file app "www"))])

(defn copy-config
  [project {:keys [config app] :as options} & args]
  ["cp" "-v" config app])

(defn build
  "Build cordova project for all platforms"
  [project {:keys [resource-root config] :as options} & args]

  ;; Copy over requisite files.
  (if resource-root
    (lmain/info (:out (apply sh (copy-tree project options)))))

  (if config 
    (lmain/info (:out (apply sh (copy-config project options)))))

  ;; Build
  (let [{:keys [out err exit]} (build-all project options)]
    (lmain/info err)
    (lmain/info out)))

(defn cordova
  "Cordova wrapper"
  {:help-arglists '([create build])
   :subtasks [#'create #'build]}
  [project subtask & args]
  (let [options (:cordova project)]
    (case subtask
      "create" (create project options args)
      "build" (build project options args)
      (do
        (lmain/info
         "Subtask" (str \" subtask \") "not found."
         (lhelp/subtask-help-for *ns* #'cordova))
        (lmain/abort)))))
