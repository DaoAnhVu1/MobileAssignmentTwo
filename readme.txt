
s3926187 - Dao Anh Vu

1. Features:

-) Authentication via email and password or Google Account.
-) Find and filter cleaning sites by criteria such as name, date.
-) Show sites on the maps.
-) Navigate through the map using address text.
-) Join cleaning sites created by others (send notifications to the site owner).
-) Users can manage their sites (both those they have joined and those they have created).
-) Users can create their sites with a name, date, time, address, and image.
-) For the address, users can navigate using the map, click on a location, and the system will automatically retrieve the address.
-) Users can upload an image to represent their sites.
-) Site owners can create a summary for the site (send notifications to all participants).
-) For sites they have joined, users can view the site summary and find a route to the site from their current location.
-) Superusers can view all the sites and users in the system.
2. Technologies:

-) Firebase authentication for allowing users to sign in with email, password, and their Google account.
-) Cloud Firestore for storing data.
-) Firebase Storage to save user-generated images.
-) Firebase Cloud Messaging to send notifications from devices to devices.
-) Maps SDK for Android to enable Google Map functionality.
-) Places API for generating places according to latlng information.
-) Direction API for calculating routes from one place to another.

3. Drawbacks:
-) The app does not allow users to change their avatars.
-) The app does not allow users to delete their sites.
-) For the Direction API, there might be a limitation in the future, restricting it to only one request
per day on the Google Cloud Platform. Currently, there is no such limitation.

4. Setup:
There is a debug.keystore in this project. Copy it and navigate to /Users/YOURUSERNAME/.android.
Paste the debug.keystore in this location. If you already have one, make sure to save it somewhere
else and use mine. Make sure to change the local properties in your IDE accordingly as well.

I share the key to find route with one of my classmate Dang Tran Huy Hoang - s3927241

Test account:
email: testuser222@gmail.com
password: testuser222

Admin account:
email: admin@gmail.com
password: admin1234
