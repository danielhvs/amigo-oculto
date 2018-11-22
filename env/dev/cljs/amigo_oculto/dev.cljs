(ns ^:figwheel-no-load amigo-oculto.dev
  (:require
    [amigo-oculto.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
