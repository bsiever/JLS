# Releasing JLS

Every release is triggered by pushing a version tag. The GitHub Actions workflow
then builds three artifacts automatically and publishes them as a GitHub Release:

| Artifact | Description |
|----------|-------------|
| `JLS.jar` | Cross-platform runnable JAR (requires Java 8+) |
| `JLS_macOS.zip` | macOS application bundle (via jpackage, bundled JRE) |
| `JLS_windows.exe` | Windows installer (via jpackage, bundled JRE) |

---

## How to publish a release

### 1. Update the version number in source

Edit [`src/jls/JLSInfo.java`](src/jls/JLSInfo.java) and increment `vers` and/or
`release` as appropriate:

```java
public static final int vers    = 4;
public static final int release = 11;   // ← bump this
```

Commit that change:

```bash
git add src/jls/JLSInfo.java
git commit -m "Bump version to 4.11"
git push
```

### 2. Create and push a matching version tag

The tag name **must** match the version you set in step 1:

```bash
git tag v4.11
git push origin v4.11
```

That push triggers the workflow. The tag becomes the release name; the workflow
strips the leading `v` to produce the jpackage version string (`4.11`).

### 3. Monitor the build

Open the **Actions** tab in the GitHub repository. The workflow has four jobs:

```
build-jar ──┬── build-macos ──┐
            └── build-windows ─┴── create-release
```

`build-macos` and `build-windows` run in parallel once the JAR is ready.
Total build time is typically 10–15 minutes.

### 4. Verify the release

When the workflow finishes, a release named **JLS 4.11** appears on the
[Releases page](https://github.com/bsiever/JLS/releases) with all three files
attached.

---

## macOS code signing (optional but recommended)

Without a Developer ID certificate, macOS Gatekeeper will block the app on
first launch. Users can still right-click → **Open** to bypass the warning, but
signing removes this friction and is required for notarization.

### What you need

- An Apple Developer Program membership.
- A **Developer ID Application** certificate issued by Apple.

### Step 1 — Export the certificate from Keychain Access

1. Open **Keychain Access** on your Mac.
2. In the **login** keychain, find the certificate named
   **Developer ID Application: Your Name (TEAM_ID)**.
3. Right-click it and choose **Export "Developer ID Application: …"**.
4. Save it as a `.p12` file and set a strong export password when prompted.

### Step 2 — Base64-encode the .p12 file

```bash
base64 -i /path/to/certificate.p12 | pbcopy
```

This copies the base64 string to the clipboard.

### Step 3 — Add secrets to the GitHub repository

In the repository go to **Settings → Secrets and variables → Actions → New
repository secret** and add all five secrets:

| Secret name | Value |
|-------------|-------|
| `MACOS_CERTIFICATE` | The base64 string from step 2 |
| `MACOS_CERTIFICATE_PASSWORD` | The export password you set in step 1 |
| `APPLE_ID` | Your Apple ID email, e.g. `you@example.com` |
| `APPLE_TEAM_ID` | Your 10-character team ID — visible at [developer.apple.com/account](https://developer.apple.com/account) under Membership |
| `APPLE_APP_PASSWORD` | An app-specific password — generate one at [appleid.apple.com](https://appleid.apple.com) under Sign-In and Security → App-Specific Passwords |

> **Do not commit the .p12 file or passwords to the repository.**

### Step 4 — Push a new release tag

That's it. On the next tag push the workflow will:

1. Import the certificate into a temporary keychain and sign the app with `--mac-sign`
2. Submit the signed zip to Apple's notary service and wait for approval (typically 1–3 minutes)
3. Staple the notarization ticket to `JLS.app` so Gatekeeper accepts it offline
4. Re-zip and publish — users can open the app directly with no warnings

### How the signing and notarization work in the workflow

The relevant steps in `.github/workflows/release.yml` are:

1. **Import Developer ID certificate into temporary keychain** — decodes the
   base64 secret, creates a short-lived keychain in `$RUNNER_TEMP`, imports the
   `.p12`, and grants `codesign` and `jpackage` access without UI prompts.
2. **Build macOS app image** — calls jpackage with `--mac-sign`; jpackage
   locates the single Developer ID certificate in the temporary keychain automatically.
3. **Notarize and staple macOS app** — submits the signed zip to Apple's notary
   service with `xcrun notarytool submit --wait`, then staples the returned
   ticket with `xcrun stapler staple` and re-zips.
4. **Remove temporary signing keychain** — runs unconditionally (even on build
   failure) to ensure the certificate is not left on the runner.

---

## Installing an unsigned macOS build

Without signing and notarization, macOS Ventura (13) and later will report
the app as **"damaged and can't be opened"** when it is downloaded from the
internet. This is a Gatekeeper quarantine warning, not actual damage.

To run the app, open Terminal and run this command after unzipping:

```bash
xattr -cr /path/to/JLS.app
```

Then double-click the app normally. You only need to do this once per
downloaded copy. Setting up the `MACOS_CERTIFICATE` secret (see above)
eliminates this step entirely for your users.

---

## Deleting or re-running a release

To redo a release for an existing tag:

1. Delete the release from the GitHub Releases page (keep or delete the tag).
2. Delete the tag locally and remotely if you want to re-push it:
   ```bash
   git tag -d v4.11
   git push origin :refs/tags/v4.11
   ```
3. Re-push the tag to trigger a fresh build.
