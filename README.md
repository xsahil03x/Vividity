# Vividity

A baby photo sharing app.

## How to run the app 

- Clone the repository
- Build app
- run the `./gradlew :app:installRelease` command

(No need to create any separate project or anything)

## Features

- Login using Google and twitter
- View and add Photos
- Image cropping before uploading
- Comment on Photos
- Comment screening using cloud functions
- Delete your own comments
- Notifications for users you are following using cloud function triggered cloud function
- Follow / unfollow a user from profile page

## What all is used?

- Firebase
- Google recommended MVVM pattern
- LiveData, MutableLiveData, MediatorLiveData
- Home Screen Widgets
- IntentServices
