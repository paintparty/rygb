# RYGB / DEMO

Interactive demo site for RYGB color notation.

[Viewable here.](https://paintparty.github.com/rygb)

Built in Clojure(Script) with the help of [Reagent](https://github.com/reagent-project), [Re-frame](https://github.com/Day8/re-frame), [Garden](https://github.com/noprompt/garden), [Figwheel](https://figwheel.org), [Gentium Basic](https://fonts.google.com/specimen/Gentium+Basic), [Source Code Pro](https://fonts.google.com/specimen/Source+Code+Pro), and [conic-gradient](https://github.com/leaverou/conic-gradient).

Current alpha implementations of RYGB in both JS and Clojure(Script) can be found at the following repos:
<br>
[rygb-js](https://github.com/paintparty/rygb-js)
<br>
[rygb-cljc](https://github.com/paintparty/rygb-cljc)


## Development Mode

### Compile css:

Compile css file once.

```
lein garden once
```

Automatically recompile css file on change.

```
lein garden auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

## License
MIT
