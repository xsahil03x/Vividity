// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Database triggered firebase cloud function for sending FCM push notification to followers
exports.sendNewPhotoNotification = functions.database.ref("/photos/{photoId}").onCreate((snapshot, context) => {

  const photoData = snapshot.val();
  const photoId = context.params.photoId;

  if (!photoData) {
    return console.log('No photoData Data');
  }

  const uploaderId = photoData.uploaderId;
  console.log(`New Photo added to database by ${photoData.uploader}. caption -> ${photoData.caption}`);

  var payload = {
    data: {
      message: `New Photo uploaded by ${photoData.uploader}`,
      ticker: "New photo by user's you follow",
      uploaderId: uploaderId,
      uploader: photoData.uploader,
      type: 'NEW_PHOTO',
      photoId: photoId,
      timestamp: `${photoData.timestamp}`
    }
  };

  return admin.database().ref(`/users`)
    .orderByChild(`follows/${uploaderId}/notification`)
    .equalTo(true).once('value')
    .then(snapshot => {
      var tokens = [];
      if(snapshot) {
        snapshot.forEach(childSnapshot => {
          console.log("fcmToken: ", childSnapshot.child('fcmToken').val());
          tokens.push(childSnapshot.child('fcmToken').val());
        });
      }
      return admin.messaging().sendToDevice(tokens, payload);
    });

});

// Sanitizing comments. Screen offensive words
exports.sanitizeComments = functions.database.ref("/comments/{photoId}/{commentId}").onCreate((snapshot, context) => {
  const commentId = context.params.commentId;
  const post = snapshot.val();
  console.log('commentId: ' + commentId + ": " + post.text);

  if (post.sanitized) {
    return null;
  }

  post.sanitized = true;
  post.text = sanitize(post.text);

  return snapshot.ref.update(post);
});

// Sanitizing comments. Screen offensive words
exports.updateCommentCount = functions.database.ref("/comments/{photoId}").onWrite((change, context) => {
  const photoId = context.params.photoId;
  const numberOfComments = change.after.numChildren();
  console.log(`Comment count updated for ${photoId} => ${numberOfComments}`);

  return admin.database().ref(`/photos/${photoId}`).update({commentsCount: numberOfComments});
});

function sanitize(s) {
  var sanitizedText = s;
  sanitizedText = sanitizedText.replace(/\bstupid\b/ig, "st***d");
  sanitizedText = sanitizedText.replace(/\bcrazy\b/ig, "cr**y");
  sanitizedText = sanitizedText.replace(/\bpoop\b/ig, "p**p");
  sanitizedText = sanitizedText.replace(/\bfuck\b/ig, "f**k");
  sanitizedText = sanitizedText.replace(/\bass\b/ig, "a**");
  return sanitizedText
}