#TODO
I am in the middle of refactoring to Compose, to greatly simplify logic.

Only test on a real device, as Compose TextFields are broken on emulator at time of writing.

# Architecture

This project follows MVVM with Android Jetpack.
It uses Rx rather than Coroutines,
as parts of Coroutines were not ready for production at time of starting development.

Activities and Fragments hold business logic in AndroidX ViewModels.

# TODOs:

Each word pair can have tags, and you can filter by tags.
You can search by tags, and also group by tags. Group by uses the same UI (book items) as vocab groups do currently
