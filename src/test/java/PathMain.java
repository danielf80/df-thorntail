import java.nio.file.Path;
import java.nio.file.Paths;

public class PathMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Path path = Paths.get("C:\\Temp\\imgs\\01 - January 2018 (myphotopack.com).jpg");
		
		System.out.println(path.getFileName());
		System.out.println(path.toAbsolutePath().toString());
	}

}
