package sttp.client.asynchttpclient.zio

import sttp.client.{NothingT, Streams, SttpBackend}
import sttp.client.impl.zio._
import sttp.client.internal._
import sttp.client.testing.ConvertToFuture
import sttp.client.testing.streaming.StreamingTest
import zio.{Chunk, Task}
import zio.stream.Stream

class AsyncHttpClientZioStreamingTest extends StreamingTest[Task, ZioStreams] {
  override val streams: ZioStreams = ZioStreams

  override implicit val backend: SttpBackend[Task, ZioStreams, NothingT] =
    runtime.unsafeRun(AsyncHttpClientZioBackend())
  override implicit val convertToFuture: ConvertToFuture[Task] = convertZioTaskToFuture

  override def bodyProducer(arrays: Iterable[Array[Byte]]): Stream[Throwable, Byte] =
    Stream.fromChunks(arrays.map(Chunk.fromArray).toSeq: _*)

  override def bodyConsumer(stream: Stream[Throwable, Byte]): Task[String] =
    stream.runCollect.map(bytes => new String(bytes.toArray, Utf8))
}