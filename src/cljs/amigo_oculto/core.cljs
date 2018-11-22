(ns amigo-oculto.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views
(def db (atom {:qtd 1 :amigos [
                               {:nome "daniel" :email "email-daniel"} 
                               {:nome "norvan" :email "email-norvan"} 
                               {:nome "pablo" :email "email-pablo"} 
                               {:nome "joao" :email "email-joao"} 
                               {:nome "solange" :email "email-solange"}]}))

(defn anima [c]
  (if (> c 0)
    (do 
      (swap! db dissoc :resultado)
      (js/setTimeout (fn [] (doall 
                             (swap! db (fn [db] (assoc db :amigos (shuffle (:amigos db)))))
                             (anima (dec c)))) 10))
    (swap! db #(assoc % :resultado (vec (map :nome (:amigos %)))))))

(defn entradas [qtd amigos]
  [:div
   [:input {:type "button" :on-click #(swap! db (fn [db] (assoc db :qtd (inc (:qtd db))))) :value "+"} ]
   [:input {:type "button" :on-click #(anima 100) :value "~"} ]
   (for [{:keys [nome email]} amigos]
     [:div
      [:input {:value nome}]
      [:input {:value email}]])
   #_(for [i (range qtd)]
     [:div
      [:input {:label "Nome: "}]
      [:input {:label "Email: "}]])])

(defn home-page []
  [:div 
   [:h2 "Welcome to amigo-oculto"]
   [entradas (:qtd @db) (:amigos @db)]
   (when (:resultado @db)
     [:div
      [:div (str (reduce #(str %1 " -> " %2) (:resultado @db)) " -> " (first (:resultado @db)))]])])

#_(js/setInterval (fn [] (swap! db (fn [db] (assoc db :amigos (shuffle (:amigos db)))))) 10000000)
 
;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
