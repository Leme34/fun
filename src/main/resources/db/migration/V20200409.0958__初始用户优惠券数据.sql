# public static void main(String[] args) {
#         String prefix =
#                 "INSERT IGNORE INTO `coupon_member` (\n" +
#                         "\t`coupon_id`,\n" +
#                         "\t`member_id`,\n" +
#                         "\t`coupon_channel`\n" +
#                         ")\n" +
#                         "VALUES";
# StringBuilder sb = new StringBuilder(prefix);
# String template =
#                 "('%s', '%s' , '%s'),";
# for (int i = 1; i <= 100; i++) {
#             int member_id = i;
# // 发放首单免单的
#             String row = String.format(
#                     template,
#                     1,
#                     member_id,
#                     1
#             );
# sb.append(row);
# // 购买1-10张
#             for (int j = 0; j < RandomUtils.nextInt(1, 10); j++) {
#                 int coupon_id = RandomUtils.nextInt(2, 4);
# row = String.format(
#                         template,
#                         coupon_id,
#                         member_id,
#                         2
#                 );
# sb.append(row);
# }
#         }
#         String sql = StringUtils.substringBeforeLast(sb.toString(), ",");
# System.out.println(sql);
# }


INSERT IGNORE INTO `coupon_member` (`coupon_id`, `member_id`, `coupon_channel`)
VALUES ('1', '1', '1'),
       ('2', '1', '2'),
       ('2', '1', '2'),
       ('3', '1', '2'),
       ('1', '2', '1'),
       ('2', '2', '2'),
       ('2', '2', '2'),
       ('3', '2', '2'),
       ('3', '2', '2'),
       ('1', '3', '1'),
       ('3', '3', '2'),
       ('2', '3', '2'),
       ('2', '3', '2'),
       ('3', '3', '2'),
       ('2', '3', '2'),
       ('3', '3', '2'),
       ('1', '4', '1'),
       ('2', '4', '2'),
       ('2', '4', '2'),
       ('1', '5', '1'),
       ('2', '5', '2'),
       ('3', '5', '2'),
       ('2', '5', '2'),
       ('3', '5', '2'),
       ('2', '5', '2'),
       ('2', '5', '2'),
       ('1', '6', '1'),
       ('2', '6', '2'),
       ('2', '6', '2'),
       ('2', '6', '2'),
       ('3', '6', '2'),
       ('3', '6', '2'),
       ('3', '6', '2'),
       ('1', '7', '1'),
       ('3', '7', '2'),
       ('3', '7', '2'),
       ('2', '7', '2'),
       ('2', '7', '2'),
       ('1', '8', '1'),
       ('2', '8', '2'),
       ('1', '9', '1'),
       ('3', '9', '2'),
       ('3', '9', '2'),
       ('3', '9', '2'),
       ('2', '9', '2'),
       ('1', '10', '1'),
       ('2', '10', '2'),
       ('3', '10', '2'),
       ('2', '10', '2'),
       ('2', '10', '2'),
       ('1', '11', '1'),
       ('2', '11', '2'),
       ('2', '11', '2'),
       ('2', '11', '2'),
       ('2', '11', '2'),
       ('1', '12', '1'),
       ('2', '12', '2'),
       ('3', '12', '2'),
       ('1', '13', '1'),
       ('3', '13', '2'),
       ('2', '13', '2'),
       ('1', '14', '1'),
       ('2', '14', '2'),
       ('2', '14', '2'),
       ('2', '14', '2'),
       ('3', '14', '2'),
       ('2', '14', '2'),
       ('1', '15', '1'),
       ('2', '15', '2'),
       ('3', '15', '2'),
       ('2', '15', '2'),
       ('1', '16', '1'),
       ('2', '16', '2'),
       ('2', '16', '2'),
       ('2', '16', '2'),
       ('2', '16', '2'),
       ('3', '16', '2'),
       ('2', '16', '2'),
       ('1', '17', '1'),
       ('2', '17', '2'),
       ('2', '17', '2'),
       ('2', '17', '2'),
       ('3', '17', '2'),
       ('3', '17', '2'),
       ('1', '18', '1'),
       ('3', '18', '2'),
       ('2', '18', '2'),
       ('2', '18', '2'),
       ('1', '19', '1'),
       ('2', '19', '2'),
       ('1', '20', '1'),
       ('2', '20', '2'),
       ('2', '20', '2'),
       ('3', '20', '2'),
       ('1', '21', '1'),
       ('2', '21', '2'),
       ('2', '21', '2'),
       ('3', '21', '2'),
       ('1', '22', '1'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('2', '22', '2'),
       ('1', '23', '1'),
       ('2', '23', '2'),
       ('2', '23', '2'),
       ('3', '23', '2'),
       ('2', '23', '2'),
       ('1', '24', '1'),
       ('3', '24', '2'),
       ('2', '24', '2'),
       ('1', '25', '1'),
       ('3', '25', '2'),
       ('1', '26', '1'),
       ('3', '26', '2'),
       ('2', '26', '2'),
       ('1', '27', '1'),
       ('3', '27', '2'),
       ('3', '27', '2'),
       ('1', '28', '1'),
       ('3', '28', '2'),
       ('2', '28', '2'),
       ('2', '28', '2'),
       ('1', '29', '1'),
       ('2', '29', '2'),
       ('3', '29', '2'),
       ('2', '29', '2'),
       ('1', '30', '1'),
       ('3', '30', '2'),
       ('2', '30', '2'),
       ('3', '30', '2'),
       ('1', '31', '1'),
       ('2', '31', '2'),
       ('2', '31', '2'),
       ('2', '31', '2'),
       ('3', '31', '2'),
       ('3', '31', '2'),
       ('2', '31', '2'),
       ('2', '31', '2'),
       ('1', '32', '1'),
       ('3', '32', '2'),
       ('3', '32', '2'),
       ('3', '32', '2'),
       ('1', '33', '1'),
       ('2', '33', '2'),
       ('2', '33', '2'),
       ('2', '33', '2'),
       ('3', '33', '2'),
       ('1', '34', '1'),
       ('2', '34', '2'),
       ('2', '34', '2'),
       ('2', '34', '2'),
       ('1', '35', '1'),
       ('2', '35', '2'),
       ('2', '35', '2'),
       ('3', '35', '2'),
       ('1', '36', '1'),
       ('2', '36', '2'),
       ('3', '36', '2'),
       ('2', '36', '2'),
       ('2', '36', '2'),
       ('1', '37', '1'),
       ('2', '37', '2'),
       ('2', '37', '2'),
       ('2', '37', '2'),
       ('3', '37', '2'),
       ('2', '37', '2'),
       ('2', '37', '2'),
       ('1', '38', '1'),
       ('2', '38', '2'),
       ('2', '38', '2'),
       ('3', '38', '2'),
       ('2', '38', '2'),
       ('2', '38', '2'),
       ('3', '38', '2'),
       ('1', '39', '1'),
       ('2', '39', '2'),
       ('2', '39', '2'),
       ('3', '39', '2'),
       ('1', '40', '1'),
       ('3', '40', '2'),
       ('2', '40', '2'),
       ('1', '41', '1'),
       ('3', '41', '2'),
       ('3', '41', '2'),
       ('2', '41', '2'),
       ('3', '41', '2'),
       ('2', '41', '2'),
       ('1', '42', '1'),
       ('3', '42', '2'),
       ('2', '42', '2'),
       ('1', '43', '1'),
       ('3', '43', '2'),
       ('2', '43', '2'),
       ('3', '43', '2'),
       ('1', '44', '1'),
       ('3', '44', '2'),
       ('3', '44', '2'),
       ('2', '44', '2'),
       ('1', '45', '1'),
       ('3', '45', '2'),
       ('2', '45', '2'),
       ('1', '46', '1'),
       ('2', '46', '2'),
       ('1', '47', '1'),
       ('2', '47', '2'),
       ('2', '47', '2'),
       ('2', '47', '2'),
       ('2', '47', '2'),
       ('3', '47', '2'),
       ('1', '48', '1'),
       ('3', '48', '2'),
       ('3', '48', '2'),
       ('3', '48', '2'),
       ('1', '49', '1'),
       ('3', '49', '2'),
       ('2', '49', '2'),
       ('2', '49', '2'),
       ('2', '49', '2'),
       ('3', '49', '2'),
       ('1', '50', '1'),
       ('3', '50', '2'),
       ('3', '50', '2'),
       ('3', '50', '2'),
       ('1', '51', '1'),
       ('2', '51', '2'),
       ('3', '51', '2'),
       ('3', '51', '2'),
       ('1', '52', '1'),
       ('2', '52', '2'),
       ('2', '52', '2'),
       ('1', '53', '1'),
       ('3', '53', '2'),
       ('3', '53', '2'),
       ('2', '53', '2'),
       ('2', '53', '2'),
       ('2', '53', '2'),
       ('1', '54', '1'),
       ('3', '54', '2'),
       ('2', '54', '2'),
       ('1', '55', '1'),
       ('2', '55', '2'),
       ('2', '55', '2'),
       ('2', '55', '2'),
       ('3', '55', '2'),
       ('2', '55', '2'),
       ('3', '55', '2'),
       ('1', '56', '1'),
       ('2', '56', '2'),
       ('3', '56', '2'),
       ('1', '57', '1'),
       ('2', '57', '2'),
       ('3', '57', '2'),
       ('2', '57', '2'),
       ('1', '58', '1'),
       ('2', '58', '2'),
       ('3', '58', '2'),
       ('2', '58', '2'),
       ('2', '58', '2'),
       ('1', '59', '1'),
       ('3', '59', '2'),
       ('2', '59', '2'),
       ('3', '59', '2'),
       ('3', '59', '2'),
       ('1', '60', '1'),
       ('2', '60', '2'),
       ('2', '60', '2'),
       ('1', '61', '1'),
       ('2', '61', '2'),
       ('3', '61', '2'),
       ('1', '62', '1'),
       ('3', '62', '2'),
       ('3', '62', '2'),
       ('3', '62', '2'),
       ('2', '62', '2'),
       ('2', '62', '2'),
       ('1', '63', '1'),
       ('3', '63', '2'),
       ('3', '63', '2'),
       ('2', '63', '2'),
       ('1', '64', '1'),
       ('3', '64', '2'),
       ('2', '64', '2'),
       ('1', '65', '1'),
       ('3', '65', '2'),
       ('3', '65', '2'),
       ('2', '65', '2'),
       ('2', '65', '2'),
       ('3', '65', '2'),
       ('1', '66', '1'),
       ('2', '66', '2'),
       ('2', '66', '2'),
       ('2', '66', '2'),
       ('3', '66', '2'),
       ('2', '66', '2'),
       ('1', '67', '1'),
       ('3', '67', '2'),
       ('1', '68', '1'),
       ('3', '68', '2'),
       ('2', '68', '2'),
       ('1', '69', '1'),
       ('2', '69', '2'),
       ('2', '69', '2'),
       ('3', '69', '2'),
       ('3', '69', '2'),
       ('1', '70', '1'),
       ('2', '70', '2'),
       ('2', '70', '2'),
       ('3', '70', '2'),
       ('1', '71', '1'),
       ('2', '71', '2'),
       ('2', '71', '2'),
       ('2', '71', '2'),
       ('1', '72', '1'),
       ('2', '72', '2'),
       ('3', '72', '2'),
       ('2', '72', '2'),
       ('2', '72', '2'),
       ('3', '72', '2'),
       ('1', '73', '1'),
       ('3', '73', '2'),
       ('3', '73', '2'),
       ('2', '73', '2'),
       ('3', '73', '2'),
       ('2', '73', '2'),
       ('1', '74', '1'),
       ('2', '74', '2'),
       ('2', '74', '2'),
       ('2', '74', '2'),
       ('3', '74', '2'),
       ('3', '74', '2'),
       ('1', '75', '1'),
       ('3', '75', '2'),
       ('2', '75', '2'),
       ('2', '75', '2'),
       ('1', '76', '1'),
       ('3', '76', '2'),
       ('1', '77', '1'),
       ('2', '77', '2'),
       ('2', '77', '2'),
       ('2', '77', '2'),
       ('1', '78', '1'),
       ('3', '78', '2'),
       ('2', '78', '2'),
       ('1', '79', '1'),
       ('2', '79', '2'),
       ('3', '79', '2'),
       ('3', '79', '2'),
       ('1', '80', '1'),
       ('2', '80', '2'),
       ('3', '80', '2'),
       ('2', '80', '2'),
       ('2', '80', '2'),
       ('1', '81', '1'),
       ('3', '81', '2'),
       ('3', '81', '2'),
       ('1', '82', '1'),
       ('3', '82', '2'),
       ('1', '83', '1'),
       ('3', '83', '2'),
       ('2', '83', '2'),
       ('2', '83', '2'),
       ('1', '84', '1'),
       ('3', '84', '2'),
       ('1', '85', '1'),
       ('2', '85', '2'),
       ('3', '85', '2'),
       ('3', '85', '2'),
       ('2', '85', '2'),
       ('3', '85', '2'),
       ('1', '86', '1'),
       ('2', '86', '2'),
       ('2', '86', '2'),
       ('1', '87', '1'),
       ('2', '87', '2'),
       ('1', '88', '1'),
       ('3', '88', '2'),
       ('2', '88', '2'),
       ('3', '88', '2'),
       ('2', '88', '2'),
       ('1', '89', '1'),
       ('3', '89', '2'),
       ('2', '89', '2'),
       ('3', '89', '2'),
       ('3', '89', '2'),
       ('3', '89', '2'),
       ('1', '90', '1'),
       ('3', '90', '2'),
       ('1', '91', '1'),
       ('3', '91', '2'),
       ('3', '91', '2'),
       ('3', '91', '2'),
       ('3', '91', '2'),
       ('1', '92', '1'),
       ('2', '92', '2'),
       ('2', '92', '2'),
       ('3', '92', '2'),
       ('2', '92', '2'),
       ('1', '93', '1'),
       ('2', '93', '2'),
       ('2', '93', '2'),
       ('3', '93', '2'),
       ('2', '93', '2'),
       ('2', '93', '2'),
       ('3', '93', '2'),
       ('3', '93', '2'),
       ('2', '93', '2'),
       ('1', '94', '1'),
       ('2', '94', '2'),
       ('2', '94', '2'),
       ('1', '95', '1'),
       ('3', '95', '2'),
       ('2', '95', '2'),
       ('3', '95', '2'),
       ('1', '96', '1'),
       ('2', '96', '2'),
       ('3', '96', '2'),
       ('2', '96', '2'),
       ('1', '97', '1'),
       ('2', '97', '2'),
       ('3', '97', '2'),
       ('1', '98', '1'),
       ('3', '98', '2'),
       ('2', '98', '2'),
       ('2', '98', '2'),
       ('2', '98', '2'),
       ('2', '98', '2'),
       ('1', '99', '1'),
       ('2', '99', '2'),
       ('3', '99', '2'),
       ('1', '100', '1'),
       ('3', '100', '2'),
       ('2', '100', '2'),
       ('3', '100', '2'),
       ('3', '100', '2'),
       ('2', '100', '2')
