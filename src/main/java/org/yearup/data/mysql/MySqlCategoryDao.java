package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>();

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM categories");
                ResultSet resultSet = preparedStatement.executeQuery();
                ){

            while (resultSet.next()){
                categories.add(new Category(resultSet.getInt("category_id"), resultSet.getString("name"), resultSet.getNString("description")));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        Category category = null;
        String query = " SELECT * FROM categories WHERE category_id = ?;";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1,categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    category = new Category(resultSet.getInt("category_id"), resultSet.getString("name"), resultSet.getNString("description"));
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return category;
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String query = "INSERT INTO categories (name,description) VALUES (?,?)";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ){
            preparedStatement.setString(1,category.getName());
            preparedStatement.setString(2,category.getDescription());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                resultSet.next();
                category.setCategoryId(resultSet.getInt(1));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String message = "Deletion failed";
        String query = "DELETE FROM categories WHERE category_id = ?;";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ){
            preparedStatement.setInt(1,categoryId);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
