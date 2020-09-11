# [Insert generated title of talk here]

September 1954, composer John Cage writes “45 minutes for a speaker”, a set of rules meant to compose a lecture through chance operations. Using questions like “1. Is there speech or silence?” or “4. If speech, is it old material or new?” Cage composed the speech’s content, noises and gestures.

February 2019, I generate and perform a 20-minute talk, on the fly, using clojure.spec, about clojure.spec.

[I wrote a bit about me performing this talk at ClojureD on my blog](https://jelle.io/talking-gibberish-on-a-tech-conference-4021140)

## Generate your own presentation

To get an interactive development environment run:

    clojure -A:fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    rm -rf target/public

To create a production build run:

	rm -rf target/public
	clojure -A:fig:min

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
