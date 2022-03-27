# About

A little Android app to aid with language learning - write your own pocket 'vocab book', then test yourself on its contents.

At the same time, I use this project as a playground for experimenting with architecture.

# Architecture

This project follows MVVM on a basic level, but it's also extremely experimental with other architectural ideas. 
I set out to explore some uncommon architectural patterns I found interesting, and therefore used this project as a testing ground for putting the theory into practise.

It toys with some MVI concepts, for example. 
It experiments with a 'reduxer' pattern, 
to handle the UI state of multiple highly complex views within a single page. 
In order to play with these ideas, I set out to make the UI of the main page of the app purposefully difficult to implement ;-)

In the 'compose' branches, I experiment with a re-write to Jetpack Compose, to compare how data flow can potentially be simplified in a reactive UI model.

# Language Flags

One cool feature in the app is the automatic fuzzy recognition of language names. Based on this, the app can suggest flags to use as the icon for the language, where the flag is of a country where that language is widely spoken or recognised. When a language may be associated with multiple countries, the app aims to suggest the flags of all the possible countries.

In order to make this feature work, I wrote a script to scrape and clean up data about languages and countries, and then associate them with flag icons. The results of this work were published in separate libraries:

- Associations of language and countries: https://github.com/LukeNeedham/language-countries
- Flag icons: https://github.com/LukeNeedham/circle-flags-android (an Android port of [exisiting resources](https://github.com/HatScripts/circle-flags))

# TODOs:

I am in the middle of refactoring to use props, and holding viewmode elsewhere

I think the whole `VocabGroup` should be removed. Each word should belong to a language.
Each word pair can have tags, and you can filter by tags.
Each tag has a colour, similar to vocab group colour

You can search by tags, and also group by tags. Group by uses the same UI (book items) as vocab groups do currently
