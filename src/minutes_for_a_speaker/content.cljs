(ns minutes-for-a-speaker.content)

(def trivia [{:speech "The software I wrote consists of two views: audience and presenter view."}
             {:speech "The view I see the presenter view has all text and you, the audience sees only the data and generated slides."}
             {:speech "Behind this talk software sits one re-frame store kept in sync across the two tabs via local storage."}
             {:speech "In this talk I'm using a collection of clojure.spec generators as a rules engine."}
             {:speech "Most magic is done with some markov chains and clojure.spec.generator/frequencies."}
             {:speech "Let's stop calling the use of markov chains artificial intelligence."}
             {:speech "Let's see how many people walk out during the talk."}
             {:speech "Uni-directional re-frame events sync across tabs with localstorage"}
             {:speech "I'm generating a random seed with generators and feed those into the markov chains walker."}])

(comment
  (flatten articles))

(def articles [trivia
               ;; code
               [{:code "(s/def ::short-sentence\n  (s/with-gen\n    (s/and string? #(< (count %) 50) #(not (st/blank? %)))\n    gen-markov-sentence))"}
                {:code "(defn weighted-rand-choice [m seed]\n  (assert (map? m) \"Not a map!\")\n  (let [w (reductions #(+ % %2) (vals m))\n        r (rand-int (last w))]\n    (nth (keys m) (count (take-while #( <= % r ) w)))))\n\n;; Thanks Jack!\n\n(defn generate-sentence* [start chain seed]\n  (if-let [transition (get chain start)]\n    (cons start\n          (lazy-seq (generate-sentence*\n                      (weighted-rand-choice transition seed)\n                      chain seed))))"}
                {:code "[:button {:on-click #(rf/dispatch [:generate (spcs/generate-talk)])}\n \"generate\"]"}
                {:code "(s/def ::gestures (s/and (s/coll-of ::gesture-in-time \n                                    :kind vector? :count 30)\n                         #(->> (map :timestamp %) \n                               (apply distinct?))))"}
                {:code "(s/def ::talk (s/keys :req-un [::title ::speech ::gestures ::next-slides \n                               ::slides ::slide-styles]))"}
                {:code "(re-frame/add-post-event-callback :sink ls/write-to-localstorage!)"}]

               ;; https://github.com/matthiasn/talk-transcripts/blob/master/Hickey_Rich/MaybeNot.md
               [{:quote {:txt "There is a spectrum of what you can communicate. What is straightforward to communicate, and what is not."
                         :author "Rich Hickey"}}]

               ;; https://github.com/matthiasn/talk-transcripts/blob/master/Halloway_Stuart/AgilityRobustnessClojureSpec.md
               [{:quote {:txt "So one of the things that is fun about developing with spec is: as soon as you have made a spec, and it can even be a very rudimentary one, you can then say \"give me example data\" or \"give me example functions that do this\"."
                         :author "Stuart Halloway"}}]

               ;spec-guide
               [{:speech "Each spec describes a set of allowed values."}
                {:speech "There are several ways to build specs and all of them can be composed to build more sophisticated specs."}
                {:speech "Building your own generator gives you the freedom to be either narrower and/or be more explicit about what values you want to generate."}
                {:speech "Alternately, custom generators can be used in cases where conformant values can be generated more efficiently than using a base predicate plus filtering."}
                {:speech "Spec does not trust custom generators and any values they produce will also be checked by their associated spec to guarantee they pass conformance."}
                {:speech "There are three ways to build up custom generators - in decreasing order of preference:"
                 :slide "1. Let spec create a generator based on a predicate/spec\n2.Create your own generator from the tools in clojure.spec.gen.alpha\n3.Use test.check or other test.check compatible libraries (like test.chuck)"}
                {:speech "The simplest way to start generating values for this spec is to have spec create a generator from a fixed set of options."}
                {:speech "A set is a valid predicate spec so we can create one and ask for it’s generator."
                 :code "(def kw-gen (s/gen #{:my.domain/name :my.domain/occupation :my.domain/id}))\n(gen/sample kw-gen 5)\n;;=> (:my.domain/occupation :my.domain/occupation :my.domain/name :my.domain/id :my.domain/name)"}]

               ;; Frankie Sardo https://juxt.pro/blog/posts/generative-ui-clojure-spec.html
               [{:speech "This is exactly how you would exercise your function in a unit test, using certain inputs to verify edge conditions, except in this this case is a visual test."}
                {:speech "Now, we all know generative testing is a great complement to your unit tests, so why not make use of both?"}
                {:speech "If you can generate valid inputs from your clojure.spec annotations, then throw them to your application and see how it renders them."}
                {:speech "If your job is to eat two frogs, eat the big one first."}
                {:speech "If your job is to eat a frog, eat it first thing in the morning."}
                {:speech "Ideally your schema annotations compose as your rendering function compose."}
                {:speech "So if you’ve annotated layout A and B and now you use both of them inside layout C, the latter has a schema that depends both on A and B."}
                {:speech "This process can bubble up to the root of your application R so that its schema describes all valid inputs for your application."}]


               ;; http://conan.is/blogging/a-spec-for-urls-in-clojure.html
               [{:speech "This generator isn't very good - the URLs it generates are valid, but they're all pretty similar."}
                {:speech "We need a more comprehensive generator that will produce a wide variety of URLs; after all, variety is the spice of generative testing."
                 :code "(sgen/sample (s/gen uri?) 3)\n  => \n  (#object[java.net.URI 0x2c33ebfb \"http://c4833483-df0c-42b5-9256-3a13db2639d2.com\"]\n   #object[java.net.URI 0x41621c4e \"http://41527909-73d2-4b95-9b5d-b6507c83898b.com\"]\n   #object[java.net.URI 0x1e98c9ff \"http://d585f324-7f35-4f3b-a39e-d48ea5489697.com\"])"}
                {:speech "The such-that here takes a predicate and another generator, and filters that generator's output using the predicate."}
                {:speech "This does mean that some of the generated values are thrown away, which is wasted work, but we just want to filter out the empty strings, and they're rare compared to all possible outputs of string-alphanumeric."
                 :code "(defn non-empty-string-alphanumeric\n  []\n  (sgen/such-that #(not= \"\" %) \n    (sgen/string-alphanumeric)))\n\n(sgen/generate (non-empty-string-alphanumeric))\n=> \"j1cf5jDg6toLyP\""}
                {:speech "We can make more complex generators like this by combining simpler ones."}
                {:speech "Now that we have a generator that can create non-empty strings, we can easily create non-empty vectors of non-empty url-encoded strings, and separate them with slashes."}]

               ;; https://codesai.com/2018/03/kata-generating-bingo-cards
               [{:speech "I thought that we might use this ability of randomly generating data conforming to the spec in production code."}
                {:speech "This meant that instead of writing code to randomly generating bingo cards and then testing that the results were as expected, we might describe the bingo cards using clojure.spec and then took advantage of that specification to randomly generate bingo cards using clojure.test.check's generate function."}
                {:speech "Other times I just use TDD."}
                {:speech " For a brief introduction to this wonderful library see Arne Brasseur's Introduction to clojure.spec talk."}
                {:speech "I think what I use depends a lot on how easy I feel the implementation might be."}
                {:speech "Of course we were trying to find a needle in a haystack..."}
                {:speech "Another common use of clojure.spec specs is to generate random data conforming to the spec to be used for property-based testing."}

                {:speech "This experiment was a lot of fun because we got to play with both clojure.spec and clojure/test.check, and we learned a lot."}]

               ;; http://upgradingdave.com/blog/posts/2016-06-21-random-pwd-with-spec.html
               [{:speech "I'm just wrapping my brain around it, but clojure.spec seems really useful."}
                {:speech "Introducing property-based testing."}
                {:speech "A while ago, I tinkered with using data.generators and test.check for generating random data."}
                {:speech "Oops, hmm, that didn't work."}
                {:speech "So, as you can see, s/gen can't always figure out how to create a generator function from a spec."}
                {:speech "Here's another way I came up with to write the same spec."}
                {:speech "I like this version better because it has the added bonus of ensuring that the two lower case letters chosen are unique."}
                {:speech "Ok, that looks nice."}
                {:speech "Sweet! We're heading down the home stretch."}
                {:speech "Woohoo! But wait. Look a this!"
                 :code "(gen/generate (s/gen ::password))\n;;=> clojure.lang.ExceptionInfo: Couldn't satisfy such-that predicate\n;;   after 100 tries."}
                {:speech "That generator is pretty big and intimidating."}
                {:speech "Now we can associate this custom generator with ::password for the grand finale. How cool is that?!"
                 :code "(gen/generate (s/gen ::password))\n;;=> \"^22T$^cCM!Yv$5^\""}
                {:speech "A generator is a no-arg function that returns a clojure.test.check.generators.Generator"}
                {:speech "We need a more comprehensive generator that will produce a wide variety of URLs; after all, variety is the spice of generative testing."}
                {:speech "But, oh no! Look what happens when I try to generate examples data from the ::two-symbols spec:"
                 :code "(gen/generate (s/gen ::two-symbols))\n;;=> clojure.lang.ExceptionInfo: Couldn't satisfy such-that predicate after 100 tries.\n"}
                {:speech "A spec is simply a function that takes one argument and returns true or false, a.k.a, a predicate function."}]

               ;; http://arnebrasseur.net/talks/2016-clojutre/#
               [{:speech "Namespace doesn’t have to be loaded or even exist"}
                {:speech "Let’s get started!\n\nLoad spec in your namespace, aliased to s."}
                {:speech "keep in mind ::recipe == :robochef.core/recipe"}
                {:code "(s/exercise :robochef/ingredients 2)"}
                {:quote {:author "Rich Hickey"
                         :txt "Most systems for specifying structures conflate the specification of the key set with the specification of the values designated by those keys. This is a major source of rigidity and redundancy."}}
                {:speech "s/keys will look at every key in a map, try to find a spec with that name, use it to validate the corresponding value"}
                {:speech "combine and compose"}
                {:speech "validating, conforming, runtime and compile-time checks"}]


               ;; https://blog.michielborkent.nl/2017/10/10/parsing-a-circuit-with-clojure-spec/
               [{:speech "However, to generate a variable name which looks like my input, for the sake of playing around with spec, I need to provide my own generator."}
                {:speech "Scanning through my input, I discovered that a variable name’s length is either 1 or 2 and only alphabetic characters may be used."}
                {:speech "Running the generator yields for example"
                 :code "(gen/sample varname-gen)\n ;;=> (g dv de pw hi a c j bz y)"}
                {:speech "We can now generate entire expressions"
                 :code "(gen/sample (s/gen ::expr))\n ;;=> ((NOT g -> sw) (NOT 0 -> j) (gl LSHIFT 0 -> q) (NOT 1 -> ly) (NOT 2 -> j) (ug -> o) (p RSHIFT 0 -> p) (NOT oj -> dz) (ih -> m) (NOT 5 -> fc))"}]

               ;; https://www.bradcypert.com/an-informal-guide-to-clojure-spec/
               [{:speech "There’s a lot to Clojure Spec. I’m going to cover what I consider to be the practical aspects, or the aspects that I commonly use."}
                {:speech "My goal is to make this light and comprehensible."}
                {:speech "You’re probably thinking “This is cool, but I’m a strong independent Clojure developer and I use maps."}
                {:speech "How do I make this work with maps?”"}
                {:speech "And in fact, we can go deeper. We can leverage spec.keys to compose a map specification built off of existing specifications."}
                {:speech "We can then leverage generation with the same sense of composition as a spec definition."}
                {:speech "There is so much more to Clojure Spec, but these are the parts that I love and use regularly."}
                {:speech "A declarative form to validate maps or even simple values is a wonderful addition to Clojure and hopefully you’ll end up using it, too!"}]

               ;; https://blog.jeaye.com/2017/05/31/clojure-spec/
               [{:speech "Dear Clojure devs, use clojure.spec please"}
                {:speech "Knowing how spec works and even how to use it is handy."}
                {:speech "Still, why bother spec’ing?"}
                {:speech "Please. Still use clojure.spec. Here’s why."}
                {:speech "Let’s look at some code."}
                {:speech "Automatically instrumenting every single function call, checking all the arguments, return values, and possibly :fn specs sounds pretty slow, right? You may be surprised."}]

               ;; https://adambard.com/blog/domain-modeling-with-clojure-spec/
               [{:speech "The instrument function will attach automatic spec-checking to every function in the namespace, which makes spec errors very obvious. However, note that this is (necessarily, for Clojure) run-time checking."}
                {:speech "You also need to re-run instrument when you add or change a spec."}
                {:speech "Explain takes a spec and a value, and tells you just where your value is failing to conform to the spec (or prints a nice success message if it does conform)."}
                {:speech "This gave me a lot more confidence in my implementation."}
                {:speech "Now it’s pretty straightforward to hook this up to a template generator of one description or another and come up with a nice, readable summary of the day’s HN posts."}
                {:speech "Using spec on this project was a bit overkill, not because it’s too small a project, but because I’m never going to touch it again."}
                {:speech "Here’s hoping that spec catches on as a standard for Clojure libraries!"}
                {:speech "Now that our domain is laid out, the implementation becomes a matter of filling out those function specs we crafted so nicely."}
                {:speech "With the spec done, it’s time to see how it can help us actually write the code – after all, we haven’t actually done anything yet!"}
                {:speech "The s/cat here is a bit interesting. It represents the concatenation of several, tagged values."}
                {:speech "The tags will show up in error messages thrown by spec to help point out which condition failed."}
                {:speech "s/cat, along with a few others, are part of a branch of spec called “regular expression specs”, which are beyond the scope of this article (and problem) but worth reading about anyhow."}
                {:speech "Here, I’ve used s/and to combine two specs."}
                {:speech "I define a single spec, using s/keys, which checks that keys are present in a map."}
                {:speech "I also used s/def to register the spec to a key, which must be a namespaced key."}
                {:speech "However, even though the spec will accept unqualified keywords as valid, it demands that I use namespaced keywords to define them, for reasons I’ll explain right now."}
                {:speech "We’ve already run into one of spec’s most interesting design decisions."}
                {:speech "Notice that I’ve specified nothing about what the values of these keys might be."}
                {:speech "That’s because I can’t spec the value of a feed’s :link-uri key."}]])

