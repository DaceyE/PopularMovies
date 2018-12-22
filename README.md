# Popular Movies <img src="https://github.com/DaceyE/PopularMovies/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png" height="48px" />

<div>
  <img height="400" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-36-50.png" />
  <img height="400" hspace="20"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-37-34.png" />
</div>
&nbsp
<img height="400" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/kf/Screenshot_2018-12-16-15-00-48.png" />

**S**ee the bottom of this readme for more images.

## Instructions
**A**dd key in build.gradle file (Module: app)
```gradle
buildTypes.each { it.buildConfigField 'String', 'TMDb_API_KEY','"YOUR KEY HERE"' }
```
**C**heck issues at the end of this readme.


## Summary
**A**n app built from scratch that primarily demonstrates the ability to request, process, display, and persists data (JSON in this example) from a third party service, themoviedb.org.  It consists of simple list and detail screens.  The list displays movie posters of the 20 most popular movies, 20 top rated movies, and any that the user flagged as favorite.  The detail screen contains basic information like synopsis, rating, overview of the movie.


## Specifications
**Common Requirements**  
•  Follow Java style guide, git style guide, [core app quality guide][1], and [tablet app quality guide][2].  


**User Interface - Layout**  
•  Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.  
•  UI contains a screen for displaying the details for a selected movie.  
•  Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.  
•  Movie Details layout contains a section for displaying trailer videos and user reviews.  
•  UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.  
•  Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for discovering movies. The right fragment displays the movie details view for the currently selected movie.  

**User Interface - Function**  
•  When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.  
•  When a movie poster thumbnail is selected, the movie details screen is launched.  
•  When a trailer is selected, app uses an Intent to launch the trailer.  
•  In the movies detail screen, a user can tap a button (for example, a star) to mark it as a Favorite. Tap the button on a favorite movie will unfavorite it.  

**Network API Implementation**  
•  In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.  
•  App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.  
•  App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.  

**Data Persistence**  
•  The titles and IDs of the user’s favorite movies are stored in a native SQLite database and exposed via a ContentProvider.  
•  Data is updated whenever the user favorites or unfavorites a movie. No other persistence libraries are used.  
•  When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the database.  


## Issues
•  Demo videos, images, and links to download app are forthcoming.  
•  themoviedb.org API may or may not still be functional.  I might replace it with a dummy version if it is not.  
•  Repo should be merged with the other demo applications for convenience.  
•  Upload Java style guide used.  
•  Upload Git style guide used.  


## Images
**S**creenshots are from a API 16 ZTE Reef and a 7th generation Kindle Fire because device diversity is important.

<div>
  <img height="240" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-57-05.png" />
  <img height="240" hspace="20"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-57-29.png" />
</div>
&nbsp
<div>
  <img height="400" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-38-24.png" />
  <img height="400" hspace="20"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-38-35.png" />
</div>
&nbsp

<div>
  <img height="640" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/kf/Screenshot_2018-12-16-15-11-48.png" />
  <img height="400" hspace="20"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-38-51.png" />
</div>
&nbsp

<div>
  <img height="640" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/kf/Screenshot_2018-12-16-15-12-37.png" />
  <img height="640" hspace="20" src="https://ennis.000webhostapp.com/portfolio/popular%20movies/kf/Screenshot_2018-12-16-15-13-32.png" />
</div>
&nbsp

<div>
   <img height="400"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-40-38.png"  />
   <img height="400" hspace="20"  src="https://ennis.000webhostapp.com/portfolio/popular%20movies/zr/Screenshot_2018-12-16-16-52-34.png"  />
</div>
 


[1]: https://developer.android.com/docs/quality-guidelines/core-app-quality
[2]: https://developer.android.com/docs/quality-guidelines/tablet-app-quality
