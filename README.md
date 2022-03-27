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

# TODOs:

I am in the middle of refactoring to use props, and holding viewmode elsewhere

I think the whole `VocabGroup` should be removed. Each word should belong to a language.
Each word pair can have tags, and you can filter by tags.
Each tag has a colour, similar to vocab group colour

You can search by tags, and also group by tags. Group by uses the same UI (book items) as vocab groups do currently
