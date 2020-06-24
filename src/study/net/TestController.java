package study.net;

import study.net.exception.HttpLogicException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

public class TestController {
    public void returnIndexPage(HttpResponse response, HttpRequest request) {
        try {
            Writer writer = response.charBody();
            writer.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "    <head>\n" +
                    "        <meta charset=\"UTF-8\">\n" +
                    "        <title>Hello world</title>\n" +
                    "    </head>\n" +
                    "    <body>\n" +
                    "        <h2>GET requests</h2>\n" +
                    "        <table style=\"width:100%\">\n" +
                    "            <tr>\n" +
                    "                <th>Get file</th>\n" +
                    "                <th>Send query with parameters</th>\n" +
                    "                <th>Redirect page</th>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td><a href='http://localhost:" + request.port() + "/files?filename=logger.properties'>http://localhost:" + request.port() + "/files?filename=logging.properties</a></td>\n" +
                    "                <td><a href='http://localhost:" + request.port() + "/statistic?type=average&accuracy=5&array[0]=34.3&array[1]=10.1&array[2]=32.32&array[3]=32.2'>http://localhost:" + request.port() + "/statistic?type=average&accuracy=5&array[0]=34.3&array[1]=10.1&array[2]=32.32&array[3]=32.2</a></td>\n" +
                    "                <td><a href='http://localhost:" + request.port() + "/location'>http://localhost:" + request.port() + "/location</a></td>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td>Download file from the web server</td>\n" +
                    "                <td>Send request for getting metadata with query parameters</td>\n" +
                    "                <td>Make redirect</td>\n" +
                    "            </tr>\n" +
                    "        </table>\n" +
                    "    </body>\n" +
                    "</html>");
            writer.flush();

            response.header("Content-Type", "text/html");
            response.header("Content-Type", "charset=UTF-8");
            response.header("Content-Length", String.valueOf(response.body().size()));
            response.status(200);
        } catch (IOException e) {
            throw new HttpLogicException("could not propagate html page", 500);
        }
    }

    public void statistic(HttpResponse response, HttpRequest request) {
        String type = request.getParameter("type");
        if (type == null) {
            throw new HttpLogicException("type must not be null", 400);
        }

        List<String> array = request.getParameters("array");
        if (array == null) {
            throw new HttpLogicException("values are required", 400);
        }

        String accuracy = request.getParameter("accuracy");

        int accuracyInt;
        if (accuracy == null) {
            accuracyInt = 10;
        } else {
            try {
                accuracyInt = Integer.parseInt(accuracy);
            } catch (NumberFormatException e) {
                throw new HttpLogicException("invalid accuracy", 400);
            }
        }

        double result;
        if ("sum".equals(type)) {
            result = array.stream().mapToDouble(TestController::parseDouble).average().orElse(0.0);
        } else if ("average".equals(type)) {
            result = array.stream().mapToDouble(TestController::parseDouble).sum();
        } else {
            throw new HttpLogicException("unknown operation " + type, 400);
        }

        String resultStr = String.format("%." + accuracyInt + "f", result);

        try {
            response.status(200);
            response.body().write(String.format("{\"result\": %s, \"type\": %s}", resultStr, type).getBytes(response.charset()));
            response.body().flush();

            response.header("Content-Type", "application/json");
            response.header("Content-Length", String.valueOf(response.body().size()));
        } catch (IOException e) {
            throw new HttpLogicException("unsupported exception", 500);
        }
    }

    private static double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            throw new HttpLogicException("invalid double value " + val, 400);
        }
    }

    public void getFile(HttpResponse response, HttpRequest request) {
        String filename = request.getParameter("filename");
        if (filename == null) {
            throw new HttpLogicException("invalid filename", 400);
        }
        try (InputStream input = getClass().getResourceAsStream(filename)) {
            if (input == null) {
                throw new HttpLogicException("file not found", 404);
            }
            byte[] buffer = new byte[2 << 11];
            int n;
            while ((n = input.read(buffer, 0, buffer.length)) != -1) {
                response.body().write(buffer, 0, n);
            }
            response.flush();

            response.header("Content-Type", "application/octet-stream");
            response.header("Content-Disposition", "attachment");
            response.header("Content-Disposition", "filename=\"logger.properties\"");
            response.header("Content-Length", String.valueOf(response.body().size()));

            response.status(200);
        } catch (IOException e) {
            throw new HttpLogicException("could not download file", 500);
        }
    }

    public void doRedirect(HttpResponse response, HttpRequest request) {
        response.status(301);
        response.redirect("https://google.com");
    }

}
