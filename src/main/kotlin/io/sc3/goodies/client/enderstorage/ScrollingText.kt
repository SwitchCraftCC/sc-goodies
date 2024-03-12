package io.sc3.goodies.client.enderstorage

class ScrollingText(rawScrollSize: Float) {
  private var scrollSize = rawScrollSize.coerceAtLeast(0f)

  var pos = 0f
    private set

  private var direction = 1
  private var waitTimer = SCROLL_WAIT_TIME

  fun update(delta: Float) {
    if (waitTimer > 0) {
      waitTimer -= delta
    } else {
      pos += direction * delta * SCROLL_SPEED

      // If we've reached the start or end, reverse the direction and wait a bit
      if (pos > scrollSize) {
        pos = scrollSize
        direction = -1
        waitTimer = SCROLL_WAIT_TIME
      } else if (pos < 0) {
        pos = 0f
        direction = 1
        waitTimer = SCROLL_WAIT_TIME
      }
    }
  }

  fun mouseScrolled(amount: Double) {
    pos = (pos - (amount.toFloat() * SCROLL_WHEEL_SPEED)).coerceIn(0f, scrollSize)
  }

  companion object {
    private const val SCROLL_SPEED = 0.6f
    private const val SCROLL_WHEEL_SPEED = 10f
    private const val SCROLL_WAIT_TIME = 50f
  }
}
