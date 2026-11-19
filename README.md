# StudentsMatch

**here when you need it** — an Android app that connects students who need help in a subject with students who are good at it.

> **A high-school project.** I planned, designed and built StudentsMatch on my own in 2023–2024 as my final project in the high-school software engineering track (5 units), without AI tools. It is kept here as a finished school project. 
<p>
  <img src="docs/screenshots/01-splash.png" width="200" alt="Splash screen">
  <img src="docs/screenshots/02-login.png" width="200" alt="Login">
  <img src="docs/screenshots/03-register.png" width="200" alt="Sign up with a profile photo">
  <img src="docs/screenshots/09-rate-subjects.png" width="200" alt="Rate your subjects">
</p>
<p>
  <img src="docs/screenshots/05-get-help.png" width="200" alt="Posting a help request">
  <img src="docs/screenshots/06-give-help.png" width="200" alt="Requests from students who need help">
  <img src="docs/screenshots/07-request-details.png" width="200" alt="Request details">
  <img src="docs/screenshots/08-edit-profile.png" width="200" alt="Edit profile">
</p>

## The idea

As a student I sometimes struggled with a subject and didn't know who to ask for help. StudentsMatch is a simple place where students who need help and students who can give it find each other. It's aimed at middle and high school students (grades 7–12).

1. **Sign up** with your details and a profile photo.
2. **Rate your subjects:** for each of six subjects, mark "good at it" or "need help".
3. **Ask for help:** pick a subject, describe the problem and attach a photo of the exercise.
4. **Give help:** each student sees only the requests in subjects they're good at.

## Screens

| Screen | What's on it | How it's built |
|---|---|---|
| Splash | Logo and slogan, moves on to login after 3 seconds | `CountDownTimer`, `finish()` so Back doesn't return to it |
| Login | User name and password | Checks the user in SQLite, remembers the last user name in `SharedPreferences` |
| Sign up | Name, email, birth date, phone, password and a profile photo | Field validation, `MaterialDatePicker`, camera via `TakePicture` + `FileProvider` |
| Rate subjects | Six subjects, each with "good at it" / "need help" | `Spinner`s with an `ArrayAdapter`, saved to `selections_table` |
| Home | Top bar with profile photo and edit button, bottom navigation | `MaterialToolbar`, `BottomNavigationView` switching fragments |
| Get help | Subject, description and an optional exercise photo | Fragment, `Spinner`, Android Photo Picker (`PickVisualMedia`) |
| Give help | Requests in the subjects you're good at | Fragment with a `RecyclerView`, custom adapter and view holder |
| Request details | Name, subject, description, photo, and a WhatsApp button | Reads the request passed in the `Intent`, opens a `wa.me` link |
| Edit profile | Update details, photo and password, or delete the account | `update` / `delete` on the database, back to the start after deleting |

## Planning and structure

Before writing code I planned every screen and the moves between them on paper, with my teacher and a classmate giving feedback.

**Matching:** when a student opens **Give help**, the app reads the subjects they marked "good at" from `selections_table`, then loads only the requests in those subjects from `request_table`.

**Data:** a local SQLite database with three tables.

| Table | Columns | Holds |
|---|---|---|
| `user_table` | name, email, birth date, phone, password, photo path | the users |
| `selections_table` | email, subject, choice | each user's rating for each subject |
| `request_table` | name, subject, description, image | the help requests |

```
app/src/main/java/com/example/studentsmatch/
├── StartActivity.java            splash screen
├── LoginActivity.java            log in
├── RegisterActivity.java         sign up + profile photo
├── RateActivity.java             rate your subjects
├── MainActivity.java             home: bottom navigation + top bar
├── GethelpFragment.java          post a help request
├── GivehelpFragment.java         requests you can help with
├── RequestAdapter / RequestHolder  RecyclerView list
├── HelpDescriptionActivity.java  request details
├── EditUserActivity.java         edit / delete profile
├── HelperDB.java                 SQLite schema
└── User.java, Request.java       data models
```

## Tech

| | |
|---|---|
| Language | Java |
| Tools | Android Studio, Gradle |
| UI | XML layouts, Material Components, Fragments, `RecyclerView`, `BottomNavigationView` |
| Storage | SQLite (`SQLiteOpenHelper`), `SharedPreferences` |
| Device features | Camera, Photo Picker, `FileProvider` |
| Minimum | Android 11 (API 30) |

## What I learned

- **Plan before coding.** The hardest part was designing the screens and the flow between them, so I planned it all on paper first.
- **Keep going when things break.** Problems in the development environment made me rewrite parts of the code. I learned to find answers on my own, in documentation and tutorials.
- **Document everything.** I wrote down every problem, what didn't work and what needed fixing, and it helped a lot.
- **Make the right trade-offs.** Because of the time limit I dropped one-to-one matching between two students and built an open request board instead: anyone good at the subject can answer.

## Running it

1. Open the project folder in **Android Studio**.
2. Let Gradle sync.
3. Pick an emulator or a phone with Android 11 or newer and press **Run ▶**.

## 2026 update

The app, its design and its logic are the original 2023–2024 work. In 2026, with the help of an AI coding assistant, I made it run on current Android Studio before publishing it here:

- Upgraded Gradle and the Android Gradle Plugin.
- Fixed the profile photo (changes weren't saved, and large photos crashed or appeared rotated) and two other crashes.
- Passwords are stored as salted PBKDF2 hashes (`PasswordHasher.java`) instead of plain text.
- Finished the WhatsApp button that was left unfinished in the original.
- Cleaned up naming and dead code, and added this README and screenshots.
