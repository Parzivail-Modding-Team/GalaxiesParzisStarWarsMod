package dev.pswg.gametest.client;

import dev.pswg.gametest.TestResults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes the outcome of client scenarios as a JUnit-shaped XML report.
 *
 * <p>Fabric's client GameTest runner only reports through its exit code, which cannot say which scenario
 * failed. This report has the same shape as the one Minecraft's server GameTest writes, so one reader and one
 * CI integration cover both. It is rewritten after every scenario, so a crash still leaves every scenario
 * that finished before it on disk.
 *
 * <p>The report is only touched from the client GameTest thread, which runs scenarios one after another.
 */
final class ClientTestReport
{
	/**
	 * The report file name inside the results directory.
	 */
	private static final String FILE_NAME = "client-report.xml";

	/**
	 * One finished scenario.
	 *
	 * @param className the class that declared the scenario
	 * @param name      the scenario name
	 * @param seconds   how long the scenario took
	 * @param failure   the failure message, or {@code null} when the scenario passed
	 */
	private record Result(String className, String name, double seconds, String failure)
	{
	}

	/**
	 * The scenarios finished so far in this run.
	 */
	private static final List<Result> RESULTS = new ArrayList<>();

	/**
	 * Prevents instantiation.
	 */
	private ClientTestReport()
	{
	}

	/**
	 * Records one finished scenario and rewrites the report.
	 *
	 * @param className the class that declared the scenario
	 * @param name      the scenario name
	 * @param seconds   how long the scenario took
	 * @param failure   the failure message, or {@code null} when the scenario passed
	 *
	 * @throws IOException if the report cannot be written
	 */
	static void record(String className, String name, double seconds, String failure) throws IOException
	{
		RESULTS.add(new Result(className, name, seconds, failure));
		write(TestResults.resultsDirectory().resolve(FILE_NAME));
	}

	/**
	 * Writes every recorded scenario to a file.
	 *
	 * @param file the report file
	 *
	 * @throws IOException if the report cannot be built or written
	 */
	private static void write(Path file) throws IOException
	{
		try
		{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element outer = document.createElement("testsuite");
			Element suite = document.createElement("testsuite");
			outer.appendChild(suite);
			document.appendChild(outer);

			for (var result : RESULTS)
			{
				Element testCase = document.createElement("testcase");
				testCase.setAttribute("name", result.name());
				testCase.setAttribute("classname", result.className());
				testCase.setAttribute("time", Double.toString(result.seconds()));

				if (result.failure() != null)
				{
					Element failure = document.createElement("failure");
					failure.setAttribute("message", result.failure());
					testCase.appendChild(failure);
				}

				suite.appendChild(testCase);
			}

			Files.createDirectories(file.getParent());

			var transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(file.toFile()));
		}
		catch (ParserConfigurationException | TransformerException exception)
		{
			throw new IOException("Cannot write the client test report " + file, exception);
		}
	}
}
