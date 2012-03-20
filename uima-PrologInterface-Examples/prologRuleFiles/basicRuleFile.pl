getFS(Y) :- findall([X,L,[]],cas:property(type,X,L),Y).
getF([],[]).
getF([[X,Y,Z]|List],[annotator(X,Y,K)|R]) :- findall(feature(N,V),cas:property(X,N,V),K),getF(List,R).
queryPrologCas(X) :- getFS(Y),getF(Y,X).


