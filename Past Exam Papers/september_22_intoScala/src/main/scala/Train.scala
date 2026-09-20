
  object Train {
    val SIZE_LIMIT = 5
  }

  trait Train {
    @throws[InterruptedException]
    def trip(id: Int): Unit

    @throws[InterruptedException]
    def beginTrip(): Unit

    @throws[InterruptedException]
    def endTrip(): Unit
  }


