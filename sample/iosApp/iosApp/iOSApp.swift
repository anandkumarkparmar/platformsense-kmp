import SwiftUI
import commonApp

@main
struct iOSApp: App {

    init() {
        // Initialize PlatformSense with the iOS wiring before any UI is created.
        IosPlatformSenseWiringKt.initializePlatformSense()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
