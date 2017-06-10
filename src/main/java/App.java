import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		logger.info("app start");

		Observable
				.create((ObservableEmitter<Socket> emitter) -> {
					try {
						ServerSocket serverSocket = new ServerSocket(8080);
						while (true) {
							Socket socket = serverSocket.accept();
							emitter.onNext(socket);
						}
					} catch (Exception e) {
						emitter.onError(e);
					}
				})
				.observeOn(Schedulers.newThread())
				.subscribe(new Observer<Socket>() {
					@Override
					public void onSubscribe(Disposable d) {
					}

					@Override
					public void onNext(Socket socket) {
						try {
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
							bufferedReader.lines().forEach(logger::info);
						} catch (IOException e) {
							logger.error("{}", e);
						}
					}

					@Override
					public void onError(Throwable e) {
						logger.error("{}", e);
					}

					@Override
					public void onComplete() {
						logger.info("complete");
					}
				});

		logger.info("app end");
	}
}
