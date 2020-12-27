# Architecture

This project follows MVVM with Android Jetpack.
It uses Rx rather than Coroutines,
as parts of Coroutines were not ready for production at time of starting development.

Activities and Fragments hold business logic in AndroidX ViewModels.

# TODOs:

I think the whole `VocabGroup` should be removed. Each word should belong to a language.
Each word pair can have tags, and you can filter by tags.
Each tag has a colour, similar to vocab group colour

You can search by tags, and also group by tags. Group by uses the same UI (book items) as vocab groups do currently
