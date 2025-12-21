import { render, screen } from '@testing-library/react';
import Payments from './page';
import { data as fixtureData } from './data';

const customTableMock = jest.fn(({ columns, data, tabName }) => (
  <div
    data-testid="mock-data-table"
    data-tabname={tabName}
    data-rows={data.length}
    data-columns={columns.length}
  >
  </div>
));

jest.mock('@/components/customUIComponents/CustomDataTable', () => ({
  CustomDataTable: (props) => customTableMock(props),
}));

jest.mock('./columns', () => ({
  getPaymentsHistory: jest.fn(() => [
    { accessorKey: 'id', header: 'ID' },
    { accessorKey: 'amount', header: 'Amount' },
    { accessorKey: 'date', header: 'Date' },
  ]),
}));

jest.mock('@/lib/utils/helper', () => ({
  getDistinctCategories: jest.fn(() => ['A', 'B', 'C']),
}));

describe('Payments Unit test cases', () => {
  it('should render the mocked data-table with correct props', () => {
    render(<Payments />);
    const table = screen.getByTestId('mock-data-table');
    expect(table).toBeInTheDocument();
    expect(customTableMock).toHaveBeenCalledTimes(1);
    const propsPassed = customTableMock.mock.calls[0][0];
    expect(propsPassed.tabName).toBe('Payment History');
    expect(propsPassed.data).toHaveLength(fixtureData.length);
    expect(propsPassed.data).toEqual(expect.arrayContaining(fixtureData));
    expect(propsPassed.columns).toHaveLength(3);
  });
});
