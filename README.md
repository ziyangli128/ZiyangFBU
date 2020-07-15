
## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
Builds an online community that users can share their outfits by creating posts with images, descriptions, and tags. Users can like, collect, and search for the outfits they want. They can follow others and see the most trending outfits online.

### App Evaluation
- **Category:** Social Networking / Entertainment
- **Mobile:** This app would be primarily developed for mobile.
- **Story:** Users can like, collect, and search for the outfits they want. They can follow others and see the most trending outfits online.
- **Market:** Any individual could choose to use this app, and to keep it a safe environment.
- **Habit:** This app could be used as often or unoften as the user wanted depending on how deep their social life is, and what exactly they're looking for.
- **Scope:** Large potential for use with Facebook, Instagram, or other social networking applications.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Main activity: shows posts from others to the user
* User is able to share post to the community (photos and texts)
* User is able to like, favorite, and leave comments on a post
* User is able to follow others
* Collect the posts that get the highest likes in a different area, so that users can search for these outfits and get inspired from them.


### 2. Screen Archetypes

* Login 
* Signup - User signs up or logs into their account
* Profile Screen
   * Allows user to upload a photo and fill in information that is interesting to them and others
   * Shows the posts by current user
   * (optional) shows the posts liked or favorited by the current user
   * Allows user to log out
* Home Screen
   * Following screen
      * Sub screen that shows the posts created by the following of the current user
   * Trending/hot screen
      * Sub screen that shows the posts with highest likes, comment or favorites
   * Nearby screen
      * Sub screen that shows the posts created by users nearby in physical location
   * Search screen
      * Allows user to search for specific categories of posts according to their style tags
* Create/Compose Screen
   * Allows user to create a post and publish to the community
   * At least an image, a description, and a tag is required upon creating a post
* Details Screen
   * Displayed when a post is clicked
   * Shows the post with all images and full description
   * Shows the comments under this post
   * Users are able to like, favorite, or comment on the post.
   * (optional) Users can share the post to other platforms like facebook


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Compose
* Profile

## Wireframes
<img src="https://github.com/ziyangli128/ZiyangFBUapp/blob/master/app%20sketch.jpg" width=800><br>

## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | postId        | String   | unique id for the user post (default field) |
   | author        | Pointer to User| post author |
   | image         | File     | image that user posts (required)|
   | title         | String   | title or short caption for the post (required)|
   | description   | String   | post description by author |
   | tags          | Array of Strings| tags added to the post (required)|
   | comments      | Array    | comments under this post|
   | commentsCount | Number   | number of comments that has been posted to a post |
   | likesCount    | Number   | number of likes for the post |
   | favoritesCount| Number   | number of favorites for the post|
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | userId        | String   | unique id for the user (default field) |
   | username      | String   | username of the user (required) |
   | email         | String   | email address of the user (required)|
   | phoneNumber   | String   | phone number of the user|
   | password      | String   | password for the user (required) |
   | profileImage  | File     | profile image for the user (set to default image if not uploaded)|
   | description   | String   | description for this user|
   | followingsCount| Number  | number of followings of the user |
   | followersCount| Number   | number of followers of the user |
   | followings    | Array of Pointers of Users| followings of the user|
   | followers     | Array of Pointers of Users| followers of the user|
   | createdAt     | DateTime | date when user is created (default field)|
   
#### Comment

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | commentId     | String   | unique id for the comment (default field) |
   | commentedPost | Pointer to Post| id of the commented post|
   | author        | Pointer to User| comment author|
   | comment       | String   | String comment (required)|
   | createdAt     | DateTime | date when comment is created (default field)|


### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
         ```swift
         let query = PFQuery(className:"Post")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
