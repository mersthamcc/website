package cricket.merstham.graphql.services;

import cricket.merstham.graphql.dto.spond.MatchFeePayment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SpondUploadService {

    private static final Logger LOG = LogManager.getLogger(SpondUploadService.class);

    public List<MatchFeePayment> uploadExcelFile(InputStream inputStream) throws IOException {
        var payments = new ArrayList<MatchFeePayment>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        payments.addAll(readSheet(workbook, "Club payments"));
        payments.addAll(readSheet(workbook, "Group payments"));

        return payments;
    }

    private Collection<? extends MatchFeePayment> readSheet(
            XSSFWorkbook workbook, String sheetName) {
        var payments = new ArrayList<MatchFeePayment>();

        XSSFSheet sheet = workbook.getSheet(sheetName);

        for (var row : sheet) {
            if (row.getRowNum() == 0) continue;
            payments.add(rowToPayment(row));
        }

        return payments;
    }

    private MatchFeePayment rowToPayment(Row row) {
        return MatchFeePayment.builder()
                .id(row.getCell(0).getStringCellValue())
                .paymentDate(cellToDate(row.getCell(1)))
                .price(getPrice(row, 3))
                .familyDiscount(getPrice(row, 4))
                .gross(getPrice(row, 5))
                .fees(getPrice(row, 6))
                .net(getPrice(row, 7))
                .paymentDescription(
                        row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                .getStringCellValue())
                .product(
                        row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                .getStringCellValue())
                .memberName(
                        row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                .getStringCellValue())
                .payerName(
                        row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                .getStringCellValue())
                .payoutDate(cellToDate(row.getCell(12)))
                .link(
                        URI.create(
                                row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                        .getStringCellValue()))
                .build();
    }

    private BigDecimal getPrice(Row row, int cellNumber) {
        var price = row.getCell(cellNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        return price.getCellType().equals(CellType.BLANK)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(price.getNumericCellValue());
    }

    private String getCustomerFromRow(Row row) {
        var onBehalfOf = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        var payer = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (onBehalfOf.getCellType().equals(CellType.BLANK)) {
            return payer.getCellType().equals(CellType.BLANK)
                    ? "unknown"
                    : payer.getStringCellValue();
        }
        return onBehalfOf.getStringCellValue();
    }

    private LocalDate cellToDate(Cell cell) {
        return cell.getLocalDateTimeCellValue().toLocalDate();
    }
}
