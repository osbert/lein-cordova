# lein-cordova

A Leiningen plugin to create, configure, and build an Apache Cordova
project from other existing resources in your project.

## Usage

Put `[com.iterinc/lein-cordova "0.1.0-SNAPSHOT"]` into the `:plugins` vector of
your project.clj.

After configuring the plugin (see below), you should be able to run:

    $ lein cordova create
    $ lein cordova build

In your project.clj, create a `:cordova` top-level key:

```clojure

;; project.clj

{  ;; ...
:cordova {
  ;; Generic options affecting all commands
  :cordova-binary "/path/to/cordova"

  :app "hello"                       ;; Application name/top-level folder
  :package "io.cordova.hellocordova" ;; Fully qualified package name
  :display-title "HelloCordova"      ;; Full application class name


  :platforms [:ios :android] ;; List of platforms that should be added
      ;; NOTE: You must have the appropriate SDKs already installed!
      ;; See Cordova documentation for details
  :plugins [
      ;; Vector of plugins to be added to the project
      ;; NOTE: PLUGIN-SPEC can be any of the following:
      ;;
      ;; (full package spec w/optional version)
      ;; org.apache.cordova.console
      ;; org.apache.cordova.console@latest
      ;; org.apache.cordova.console@0.2.1
      ;;
      ;; (git URL with ref or subdir)
      ;; https://github.com/apache/cordova-plugin-console.git
      ;; https://github.com/apache/cordova-plugin-console.git#r0.2.0
      ;; https://github.com/apache/cordova-plugin-console.git#:my/sub/dir
      ;; https://github.com/apache/cordova-plugin-console.git#r0.2.0:my/sub/dir
      ;;
      ;; (local path)
      ;; ../plugin-dir
      "org.apache.cordova.console"
      ]

  ;; Resource configuration
  :resource-root "resources/public/" ;; Copy assets from here, note
      ;; trailing slash is important as it uses rsync semantics

  :config "resources/config.xml" ;; Overwrite the default Cordova
      ;; config.xml with one provided by the end-user.

  ;; Platform specific options
  ;; NOTE: Currently only Android has been tested.
  :android-sdk-path "/path/to/android-sdk/"
  }
}
```

## License

Copyright © 2015 Osbert Feng

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
