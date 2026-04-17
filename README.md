## Prerequisites
- Android Studio
- Firebase
- Algolia

## Getting Started
1. Open the project folder in Android Studio
2. Prepare the emulator or physical device
3. Click *Run*

## Firebase
1. Create a Firebase project
2. Add an Android app
3. Set the package name to `com.samsantech.souschef`
4. Download the configuration file (`google-services.json`) and copy to Android Studio project's app-level directory
5. Setup **Authentication**
   - Under Sign-in Method tab, choose *Email/Password*
   - Under Settings tab, enforce password policy
6. Initialize **Firestore**
   - Under Indexes tab, create an index with the following:
     - Collection ID: *recipes*
     - Fields to index: *userId (ascending), createdAt (descending), __name__ (descending)*
7. Initialize **Storage**

## Algolia
1. In Firebase Extensions, install **Search Firestore with Algolia**
2. Configure the following:
   - Collection Path: *recipes*
   - Indexable Fields: *userName,title,categories,tags,ingredients,userRating,averageRating,photosUrl*
   - Algolia Index Name: *souschef*
   - Algolia Application ID: *your_algolia_app_id*
   - Algolia API Key: *your_algolia_write_api_key*
3. Copy API keys and index name to `local.properties`

    ```bash
    algoliaAppId=your_app_id
    algoliaApiKey=your_api_key
    algoliaIndexName="souschef"
    ```

## Troubleshooting
1. **Illegal Argument Exception: source must not be null**
     ```bash
     FileAnalysisException: While analysing MainActivity.kt: java.lang.IllegalArgumentException: source must not be null
     ```
      This is a common issue when updating some Gradle configurations. You must update the kotlin version in `libs.version.toml`.

2. **Others**
   - You may need to revise some code syntax due to updates in Android and SDK packages.
